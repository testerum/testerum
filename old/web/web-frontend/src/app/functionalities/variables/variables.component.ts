import {Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Variable} from "./model/variable.model";
import {StringUtils} from "../../utils/string-utils.util";
import {ArrayUtil} from "../../utils/array.util";
import {VariablesService} from "../../service/variables.service";
import {AllProjectVariables} from "./model/project-variables.model";
import {Observable, Subject, Subscription} from "rxjs";
import {EnvironmentEditModalComponent} from "./environment-edit-modal/environment-edit-modal.component";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../model/prime-ng/StringSelectItem";
import {VariablesEnvironment} from "./model/variables-environment.model";

@Component({
    moduleId: module.id,
    selector: 'variables',
    templateUrl: 'variables.component.html',
    styleUrls: ['variables.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class VariablesComponent implements OnInit, OnDestroy {

    @ViewChild("modal", { static: true }) modal: ModalDirective;
    @ViewChild(EnvironmentEditModalComponent, { static: true }) environmentEditModalComponent: EnvironmentEditModalComponent;

    projectVariables: AllProjectVariables;

    disableModal: boolean = false;
    hasChanges: boolean = false;
    selectedEnvironmentName: string;
    availableEnvironmentsAsSelectedItems: SelectItem [] = [];
    variables: Array<Variable> = [];

    viewOrEditVariablesResponseSubject: Subject<void> = new Subject<void>();

    private getVariablesSubscription: Subscription;
    constructor(private variablesService: VariablesService) {
    }

    ngOnInit() {
        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: AllProjectVariables) => {
            this.initState(projectVariables);
        });
    }

    show(selectedEnvironment: string): Observable<void> {
        this.selectedEnvironmentName = selectedEnvironment;

        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: AllProjectVariables) => {
            this.initState(projectVariables);
        });
        this.modal.show();
        return this.viewOrEditVariablesResponseSubject.asObservable();
    }

    close(): void {
        this.modal.hide();
        this.viewOrEditVariablesResponseSubject.next();
    }

    private initState(projectVariables: AllProjectVariables) {
        this.hasChanges = false;
        this.projectVariables = projectVariables;

        let availableEnvironments = projectVariables.getAllAvailableEnvironments();
        this.availableEnvironmentsAsSelectedItems = [];
        for (const availableEnvironment of availableEnvironments) {
            this.availableEnvironmentsAsSelectedItems.push(new StringSelectItem (availableEnvironment))
        }
        this.initVariablesBasedOnEnvironment();
    }

    private initVariablesBasedOnEnvironment() {
        this.variables = this.projectVariables.getVariablesByEnvironmentNameMergedWithDefaultVars(this.selectedEnvironmentName);
        this.addEmptyVariableIfRequired();
    }

    ngOnDestroy(): void {
        if(this.getVariablesSubscription) this.getVariablesSubscription.unsubscribe();
        if(this.viewOrEditVariablesResponseSubject) this.viewOrEditVariablesResponseSubject.complete();
    }

    editEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.editEnvironmentName(this.selectedEnvironmentName, this.projectVariables).subscribe( (selectedEnvironment: string) => {
            this.selectedEnvironmentName = selectedEnvironment;
            this.initState(this.projectVariables);
            this.disableModal = false;
            this.hasChanges = true;
        });
    }

    addEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.addEnvironment(this.projectVariables).subscribe( (selectedEnvironment: string) => {
            if (selectedEnvironment) {
                this.selectedEnvironmentName = selectedEnvironment;
                this.projectVariables.currentEnvironment = this.selectedEnvironmentName;
            }

            this.initState(this.projectVariables);
            this.disableModal = false;
            this.hasChanges = true;
        });
    }

    onEnvironmentChange(event: any) {
        this.setCurrentVariablesToProjectEnvironment();

        this.selectedEnvironmentName = event.value;
        this.projectVariables.currentEnvironment = this.selectedEnvironmentName;
        this.variablesService.saveCurrentEnvironment(this.selectedEnvironmentName).subscribe(() => {});
        this.initVariablesBasedOnEnvironment();
    }

    getEnvironmentInfoMessage(value: string): string {
        if (value == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME) {
            return "Variables declared in this environment will be inherited by all the other environments"
        }
        if (value == AllProjectVariables.LOCAL_ENVIRONMENT_NAME) {
            return "Variables declared in this environment are not saved inside the project and they will not be commited with your tests. <br/>" +
                "<br/>" +
                "In this way, you can have your own private variables for your Testerum instance."
        }

        return null;
    }

    onKeyChange(value: string, variable: Variable) {
        variable.key = value;
        this.hasChanges = true;
        this.addEmptyVariableIfRequired();
    }

    onNewValueChange(value: Event) {
        this.hasChanges = true;
        this.addEmptyVariableIfRequired();
    }

    onNewValueKeyDown(event: Event, variable: Variable) {
        this.hasChanges = true;
        if (variable.isVariableFromDefaultEnvironment) {
            variable.isVariableFromDefaultEnvironment = false;
        }
    }

    private addEmptyVariableIfRequired() {
        if(this.variables == null) this.variables = [];

        if(this.variables.length == 0) {
            this.addEmptyVariable();
            return
        }

        let lastKeyValue = this.variables[this.variables.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.addEmptyVariable()
        }
    }

    private addEmptyVariable() {
        this.variables.push(
            new Variable()
        );
    }

    deleteVariable(variable: Variable) {
        ArrayUtil.removeElementFromArray(this.variables, variable);
        this.setCurrentVariablesToProjectEnvironment();
        this.initVariablesBasedOnEnvironment();

        this.hasChanges = true;
    }

    isEditableEnvironment(): boolean {
        return this.selectedEnvironmentName != AllProjectVariables.DEFAULT_ENVIRONMENT_NAME &&
            this.selectedEnvironmentName != AllProjectVariables.LOCAL_ENVIRONMENT_NAME;
    }

    save(): void {
        this.setCurrentVariablesToProjectEnvironment();

        let projectVariablesToSave = this.getProjectVariablesToSave()

        this.variablesService.save(projectVariablesToSave).subscribe (
            result => {
                this.hasChanges = false;
                this.close();
            }
        )
    }

    cancel(): void {
        this.close();
    }

    isDefaultOrLocalEnvironment(value: string): boolean {
        return value == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME || value == AllProjectVariables.LOCAL_ENVIRONMENT_NAME;
    }


    private setCurrentVariablesToProjectEnvironment() {
        this.projectVariables.setVariablesToEnvironment(this.selectedEnvironmentName, this.variables);
    }

    private getProjectVariablesToSave(): AllProjectVariables {
        let result = new AllProjectVariables();
        result.currentEnvironment = this.projectVariables.currentEnvironment;
        result.defaultVariables = this.projectVariables.defaultVariables;
        result.localVariables = this.getVariablesToSave(this.projectVariables.localVariables);

        for (const environment of this.projectVariables.environments) {
            let variableEnvironmentToSave = new VariablesEnvironment();
            variableEnvironmentToSave.name = environment.name
            variableEnvironmentToSave.variables = this.getVariablesToSave(environment.variables)
            result.environments.push(
                variableEnvironmentToSave
            )
        }

        return result;
    }

    private getVariablesToSave(variables: Variable[]): Variable[] {
        let result: Variable[] = [];

        for (const variable of variables) {
            if (!variable.isVariableFromDefaultEnvironment) {
                result.push(variable)
            }
        }

        return result;
    }

    isValid(): boolean {
        if(!this.areVariablesValid(this.variables)) return false;
        if(!this.areVariablesValid(this.projectVariables.defaultVariables)) return false;
        if(!this.areVariablesValid(this.projectVariables.localVariables)) return false;

        for (const environment of this.projectVariables.environments) {
            if(this.selectedEnvironmentName == environment.name) continue;
            if(!this.areVariablesValid(environment.variables)) return false;
        }
        return true;
    }

    private areVariablesValid(variales: Variable[]) {
        for (const variale of variales) {
            if(variale.isEmpty()) continue;
            if(StringUtils.isEmpty(variale.key)) return false;
        }

        return true;
    }
}
