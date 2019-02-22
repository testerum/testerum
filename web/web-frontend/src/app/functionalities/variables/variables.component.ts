import {Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Variable} from "./model/variable.model";
import {StringUtils} from "../../utils/string-utils.util";
import {ArrayUtil} from "../../utils/array.util";
import {VariablesService} from "../../service/variables.service";
import {AllProjectVariables} from "./model/project-variables.model";
import {Subscription} from "rxjs";
import {EnvironmentEditModalComponent} from "./environment-edit-modal/environment-edit-modal.component";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../model/prime-ng/StringSelectItem";

@Component({
    moduleId: module.id,
    selector: 'variables',
    templateUrl: 'variables.component.html',
    styleUrls: ['variables.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class VariablesComponent implements OnInit, OnDestroy {

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild(EnvironmentEditModalComponent) environmentEditModalComponent: EnvironmentEditModalComponent;

    projectVariables: AllProjectVariables;

    disableModal: boolean = false;
    hasChanges: boolean = false;
    selectedEnvironmentName: string;
    availableEnvironmentsAsSelectedItems: SelectItem [] = [];
    variables: Array<Variable> = [];

    private getVariablesSubscription: Subscription;
    constructor(private variablesService: VariablesService) {
    }

    ngOnInit() {
    }

    show(selectedEnvironment: string): void {
        this.selectedEnvironmentName = selectedEnvironment;

        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: AllProjectVariables) => {
            this.initState(projectVariables);
        });
        this.modal.show();
    }

    close(): void {
        this.modal.hide();
    }

    private initState(projectVariables: AllProjectVariables) {
        this.hasChanges = false;
        this.projectVariables = projectVariables;

        let availableEnvironments = projectVariables.getAllAvailableEnvironments();
        for (const availableEnvironment of availableEnvironments) {
            this.availableEnvironmentsAsSelectedItems.push(new StringSelectItem (availableEnvironment))
        }

        let selectedEnvironmentVariables: Variable[] = this.projectVariables.getVariablesByEnvironmentName(this.selectedEnvironmentName);
        if (selectedEnvironmentVariables == null) {
            this.selectedEnvironmentName = AllProjectVariables.DEFAULT_ENVIRONMENT_NAME;
            selectedEnvironmentVariables = projectVariables.defaultVariables
        }
        this.variables = selectedEnvironmentVariables;
        this.addEmptyVariableIfRequired();
    }

    ngOnDestroy(): void {
        if(this.getVariablesSubscription) this.getVariablesSubscription.unsubscribe();
    }

    editEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.editEnvironmentName(this.selectedEnvironmentName, this.projectVariables).subscribe( (selectedEnvironment: string) => {
            this.initState(this.projectVariables);
            this.selectedEnvironmentName = selectedEnvironment;
            this.disableModal = false;
        });
    }

    addEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.addEnvironment(this.projectVariables).subscribe( (selectedEnvironment: string) => {
            this.initState(this.projectVariables);
            this.selectedEnvironmentName = selectedEnvironment;
            this.disableModal = false;
        });
    }

    onNewKeyChange(value: Event) {
        this.hasChanges = true;
        this.addEmptyVariableIfRequired();
    }

    onNewValueChange(value: Event) {
        this.hasChanges = true;
        this.addEmptyVariableIfRequired();
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
    }

    isEditableEnvironment(): boolean {
        return this.selectedEnvironmentName != AllProjectVariables.DEFAULT_ENVIRONMENT_NAME &&
            this.selectedEnvironmentName != AllProjectVariables.LOCAL_ENVIRONMENT_NAME;
    }

    save(): void {
        let variablesToSave = this.variables.filter(variable => !variable.isEmpty());
        this.variablesService.save(this.projectVariables).subscribe (
            result => {
                this.hasChanges = false;
            }
        )
    }

    cancel(): void {
        this.close();
    }

    isDefaultOrLocalEnvironment(value: string): boolean {
        return value == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME || value == AllProjectVariables.LOCAL_ENVIRONMENT_NAME;
    }

    getEnvironmentInfoMessage(value: string): string {
        if (value == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME) {
            return "Variables declared in this environment will be inherited in all the other environments"
        }
        if (value == AllProjectVariables.LOCAL_ENVIRONMENT_NAME) {
            return "Variables declared in this environment will are not saved inside the project and they will not going to be commited with your tests. <br/>" +
                   "<br/>" +
                   "In this way, you can have your own private variables for your Testerum instance."
        }

        return null;
    }
}
