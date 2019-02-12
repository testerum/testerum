import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Variable} from "./model/variable.model";
import {StringUtils} from "../../utils/string-utils.util";
import {ArrayUtil} from "../../utils/array.util";
import {VariablesService} from "../../service/variables.service";
import {ProjectVariables} from "./model/project-variables.model";
import {Subscription} from "rxjs";
import {EnvironmentEditModalComponent} from "./environment-edit-modal/environment-edit-modal.component";

@Component({
    moduleId: module.id,
    selector: 'variables',
    templateUrl: 'variables.component.html',
    styleUrls: ['variables.component.scss']
})
export class VariablesComponent implements OnInit, OnDestroy {

    @ViewChild("modal") modal: ModalDirective;
    @ViewChild(EnvironmentEditModalComponent) environmentEditModalComponent: EnvironmentEditModalComponent;

    projectVariables: ProjectVariables;

    disableModal: boolean = false;
    hasChanges: boolean = false;
    selectedEnvironmentName: string;
    availableEnvironments: string[] = [];
    variables: Array<Variable> = [];

    private getVariablesSubscription: Subscription;
    constructor(private variablesService: VariablesService) {
    }

    ngOnInit() {
        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: ProjectVariables) => {
            this.initState(projectVariables);
        });
    }

    private initState(projectVariables: ProjectVariables) {
        this.projectVariables = projectVariables;

        this.selectedEnvironmentName = projectVariables.currentEnvironment ? projectVariables.currentEnvironment : ProjectVariables.DEFAULT_ENVIRONMENT_NAME;
        this.availableEnvironments = projectVariables.getAllAvailableEnvironments();
        let selectedEnvironmentVariables: Variable[] = this.projectVariables.getVariablesByEnvironmentName(this.selectedEnvironmentName);
        if (selectedEnvironmentVariables == null) {
            this.selectedEnvironmentName = ProjectVariables.DEFAULT_ENVIRONMENT_NAME;
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
        this.addEmptyVariableIfRequired();
    }

    onNewValueChange(value: Event) {
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

    show(): void {
        this.modal.show();
    }

    close(): void {
        this.modal.hide();
    }

    isEditableEnvironment(): boolean {
        return this.selectedEnvironmentName != ProjectVariables.DEFAULT_ENVIRONMENT_NAME &&
            this.selectedEnvironmentName != ProjectVariables.LOCAL_ENVIRONMENT_NAME;
    }

    save(): void {
        let variablesToSave = this.variables.filter(variable => !variable.isEmpty());
        // this.variablesService.save(variablesToSave).subscribe(
        //     result => {
        //         this.editMode = false;
        //     }
        // )
    }

    cancel(): void {
        // this.loadVariablesFromServer();
    }
}
