import {Component, OnDestroy, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Observable, Subject} from "rxjs";
import {NgForm} from "@angular/forms";
import {FormUtil} from "../../../utils/form.util";
import {ArrayUtil} from "../../../utils/array.util";
import {AllProjectVariables} from "../model/project-variables.model";
import {VariablesEnvironment} from "../model/variables-environment.model";

@Component({
    selector: 'environment-edit-modal',
    templateUrl: './environment-edit-modal.component.html',
    styleUrls: ['./environment-edit-modal.component.scss']
})
export class EnvironmentEditModalComponent implements OnDestroy {
    @ViewChild("modal", { static: true }) modal: ModalDirective;
    @ViewChild(NgForm, { static: true }) form: NgForm;

    title: string;

    newEnvironmentName: string;
    environmentNameToEdit: string;
    projectVariables: AllProjectVariables;

    selectedEnvironmentResponseSubject: Subject<string> = new Subject<string>();

    ngOnDestroy(): void {
        this.selectedEnvironmentResponseSubject.complete();
    }

    addEnvironment(projectVariables: AllProjectVariables): Observable<string> {
        this.environmentNameToEdit = null;
        this.projectVariables = projectVariables;
        this.newEnvironmentName = null;
        this.title = "Add Environment";

        this.modal.show();
        return this.selectedEnvironmentResponseSubject;
    }

    editEnvironmentName(environmentNameToEdit: string, projectVariables: AllProjectVariables): Observable<string> {
        this.environmentNameToEdit = environmentNameToEdit;
        this.projectVariables = projectVariables;
        this.newEnvironmentName = environmentNameToEdit;
        this.title = "Edit Environment";

        this.modal.show();
        return this.selectedEnvironmentResponseSubject
    }

    onEnvironmentNameChange(event: any): void {
        if (ArrayUtil.containsElement(this.projectVariables.getAllAvailableEnvironments(), this.newEnvironmentName) && this.newEnvironmentName != this.environmentNameToEdit) {
            FormUtil.addErrorToForm(this.form, "name", "a_resource_with_the_same_name_already_exist");
        }
    }

    isValid(): boolean {
        return this.form.valid
    }

    isAddEnvironmentMode(): boolean {
        return this.environmentNameToEdit == null;
    }

    save(): void {
        if(!this.isValid()) return;

        if (this.isAddEnvironmentMode()) {
            let variablesEnvironment = new VariablesEnvironment();
            variablesEnvironment.name = this.newEnvironmentName;
            this.projectVariables.environments.push(
                variablesEnvironment
            );
        } else {
            let variablesEnvironment: VariablesEnvironment = this.projectVariables.getEnvironmentByName(this.environmentNameToEdit);
            variablesEnvironment.name = this.newEnvironmentName;
            this.projectVariables.sortEnvironmentVariablesByName();
        }

        this.selectedEnvironmentResponseSubject.next(this.newEnvironmentName);
        this.modal.hide();
    }

    cancel(): void {
        this.selectedEnvironmentResponseSubject.next(this.environmentNameToEdit);
        this.modal.hide();
    }

    delete() {
        let environmentToDelete = this.projectVariables.getEnvironmentByName(this.environmentNameToEdit);
        ArrayUtil.removeElementFromArray(this.projectVariables.environments, environmentToDelete);

        this.selectedEnvironmentResponseSubject.next(AllProjectVariables.DEFAULT_ENVIRONMENT_NAME);
        this.modal.hide();
    }
}
