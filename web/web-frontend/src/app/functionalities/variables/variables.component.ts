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
    selectedEnvironment: string;
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

        this.selectedEnvironment = projectVariables.currentEnvironment ? projectVariables.currentEnvironment : ProjectVariables.DEFAULT_ENVIRONMENT_NAME;
        this.availableEnvironments = projectVariables.getAllAvailableEnvironments();
    }

    ngOnDestroy(): void {
        if(this.getVariablesSubscription) this.getVariablesSubscription.unsubscribe();
    }

    editEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.editEnvironmentName(this.selectedEnvironment, this.projectVariables).subscribe( (selectedEnvironment: string) => {
            this.initState(this.projectVariables);
            this.selectedEnvironment = selectedEnvironment;
            this.disableModal = false;
        });
    }

    addEnvironment(): void {
        this.disableModal = true;
        this.environmentEditModalComponent.addEnvironment(this.projectVariables).subscribe( (selectedEnvironment: string) => {
            this.initState(this.projectVariables);
            this.selectedEnvironment = selectedEnvironment;
            this.disableModal = false;
        });
    }

    onNewKeyChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.variables[this.variables.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.variables.push(
                new Variable()
            );
        }
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
        return this.selectedEnvironment != ProjectVariables.DEFAULT_ENVIRONMENT_NAME &&
            this.selectedEnvironment != ProjectVariables.LOCAL_ENVIRONMENT_NAME;
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
