import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ComponentRef, ViewChild} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";
import {Scenario} from "../../../../../../../model/test/scenario/scenario.model";
import {ScenarioParamType} from "../../../../../../../model/test/scenario/param/scenario-param-type.enum";
import {editor} from "monaco-editor";

@Component({
    moduleId: module.id,
    selector: 'arg-modal',
    templateUrl: 'scenario-param-modal.component.html',
    styleUrls: [
        'scenario-param-modal.component.scss'
    ],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ScenarioParamModalComponent {

    oldScenarioParam: ScenarioParam;
    newScenarioParam: ScenarioParam;
    allScenarios: Scenario[];

    modalComponentRef: ComponentRef<ScenarioParamModalComponent>;
    modalSubject: Subject<ScenarioParam|null>;

    modalTitle;
    isEditParamNameMode: boolean = false;

    @ViewChild("resourceModal") modal: ModalDirective;

    editorOptions: editor.IEditorConstructionOptions = {
        language: 'text'
    };

    constructor(private cd: ChangeDetectorRef) {
    }

    ngAfterViewInit(): void {
        this.modalTitle = this.oldScenarioParam ? "Edit Scenario Param": "Add Param to Scenario";
        if (this.oldScenarioParam == null) {
            this.isEditParamNameMode = true;
        }

        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
            this.modalSubject = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    isValid() {
        return false;
    }

    isJsonValueType(): boolean {
        return this.newScenarioParam.type == ScenarioParamType.JSON;
    }

    onEditParamName() {
        this.isEditParamNameMode = true;
    }

    getRenameNameDescription(): string {
        return "Renaming the name of this parameter will be renamed in the other scenarios too"
    }

    getParamTypes(): ScenarioParamType[]  {
        return ScenarioParamType.enums;
    }

    onParamTypeChange(newScenarioParamType: ScenarioParamType) {
        this.newScenarioParam.type = newScenarioParamType;
        this.refresh();
    }

    onValueChange(newValue: string) {
        this.newScenarioParam.value = newValue;
        this.refresh();
    }

    onCancel() {
        this.modalSubject.next(null);
        this.modal.hide();
    }

    onOk() {
        this.modalSubject.next(this.newScenarioParam);
        this.modal.hide();
    }
}
