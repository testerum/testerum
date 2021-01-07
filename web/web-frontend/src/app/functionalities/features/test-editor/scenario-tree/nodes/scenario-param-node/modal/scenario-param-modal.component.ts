import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ComponentRef, ViewChild} from "@angular/core";
import {ModalDirective} from "ngx-bootstrap";
import {Subject} from "rxjs";
import {ScenarioParam} from "../../../../../../../model/test/scenario/param/scenario-param.model";
import {Scenario} from "../../../../../../../model/test/scenario/scenario.model";
import {ScenarioParamType} from "../../../../../../../model/test/scenario/param/scenario-param-type.enum";
import {editor} from "monaco-editor";
import {NgForm} from "@angular/forms";
import {ArrayUtil} from "../../../../../../../utils/array.util";
import {FormUtil} from "../../../../../../../utils/form.util";
import {
    ScenarioParamChangeModel,
    ScenarioParamModalResultModelAction
} from "./model/scenario-param-change.model";
import {AreYouSureModalEnum} from "../../../../../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../../../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";

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

    oldParam: ScenarioParam;
    newParam: ScenarioParam;
    allScenarios: Scenario[];
    currentScenario: Scenario;

    modalComponentRef: ComponentRef<ScenarioParamModalComponent>;
    modalSubject: Subject<ScenarioParamChangeModel>;

    modalTitle;
    otherParamsName: string[] = [];
    isEditParamNameMode: boolean = false;

    @ViewChild("resourceModal", { static: true }) modal: ModalDirective;
    @ViewChild(NgForm, { static: true }) form: NgForm;

    editorOptions: editor.IEditorConstructionOptions = {
        language: 'text'
    };

    constructor(private cd: ChangeDetectorRef,
                private areYouSureModalService: AreYouSureModalService) {
    }

    ngAfterViewInit(): void {
        this.modalTitle = this.oldParam ? "Edit Scenario Param": "Add Param to Scenario";
        if (this.oldParam == null) {
            this.isEditParamNameMode = true;
        }

        this.initOtherParamsName();

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

    private initOtherParamsName() {
        let otherParamsName: string[] = [];
        for (const param of this.currentScenario.params) {
            if (param !== this.oldParam) {
                otherParamsName.push(param.name);
            }
        }
        this.otherParamsName = otherParamsName;
    }

    isValid() {
        return this.form.valid;
    }

    isJsonValueType(): boolean {
        return this.newParam.type == ScenarioParamType.JSON;
    }

    onEditParamName() {
        this.isEditParamNameMode = true;
    }

    getRenameNameDescription(): string {
        return "Renaming the name of this parameter will be renamed in the other scenarios too"
    }

    onNameChange(newName: string) {
        if (ArrayUtil.containsElement(this.otherParamsName, newName)) {
            FormUtil.addErrorToForm(this.form, "name", "parameter_with_the_same_name_already_exist");
        } else {
            this.newParam.name = newName;
        }
    }

    getParamTypes(): ScenarioParamType[]  {
        return ScenarioParamType.enums;
    }

    onParamTypeChange(newScenarioParamType: ScenarioParamType) {
        this.newParam.type = newScenarioParamType;
        this.refresh();
    }

    onValueChange(newValue: string) {
        this.newParam.value = newValue;
        this.refresh();
    }

    deleteAction() {
        this.areYouSureModalService.showAreYouSureModal(
            "Delete",
            "Are you sure you want to delete this parameter?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                let result = new ScenarioParamChangeModel();
                result.action = ScenarioParamModalResultModelAction.DELETE;
                result.oldParam = this.oldParam;

                this.modalSubject.next(result);
                this.modal.hide();
            }
        });
    }

    onCancel() {
        let result = new ScenarioParamChangeModel();
        result.oldParam = this.oldParam;
        result.action = ScenarioParamModalResultModelAction.CANCEL;

        this.modalSubject.next(result);
        this.modal.hide();
    }

    onOk() {
        let result = new ScenarioParamChangeModel();
        result.action = this.oldParam == null ? ScenarioParamModalResultModelAction.ADD : ScenarioParamModalResultModelAction.UPDATE;
        result.oldParam = this.oldParam;
        result.newParam = this.newParam;

        this.modalSubject.next(result);
        this.modal.hide();
    }
}
