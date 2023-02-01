import {EventEmitter, Injectable} from "@angular/core";
import {StepCallEditorContainerModel} from "./model/step-call-editor-container.model";
import {JsonTreeContainer} from "../json-tree/model/json-tree-container.model";
import {StepCall} from "../../../model/step/step-call.model";
import {ComposedStepDef} from "../../../model/step/composed-step-def.model";
import {StepCallContainerModel} from "./model/step-call-container.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {SubStepsContainerModel} from "./model/sub-steps-container.model";
import {StepCallTreeUtil} from "./util/step-call-tree.util";
import {ArrayUtil} from "../../../utils/array.util";
import {StepCallContainerComponent} from "./nodes/step-call-container/step-call-container.component";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UndefinedStepDef} from "../../../model/step/undefined-step-def.model";

@Injectable()
export class StepCallTreeComponentService {
    jsonTreeModel: JsonTreeModel;
    stepCalls: Array<StepCall> = [];

    containerPath: Path;
    isEditMode: boolean;
    areManualSteps: boolean;
    isManualExecutionMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    stepCallOrderChangeEventEmitter: EventEmitter<void> = new EventEmitter<void>();
    warningRecalculationChangesEventEmitter: EventEmitter<void> = new EventEmitter<void>();
    changeEventEmitter: EventEmitter<void>;

    currentStepCallEditorModel: StepCallEditorContainerModel;

    id: string = this.guid();

    private selectedStep: StepCallContainerComponent;

    private guid() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    }

    setEditMode(editMode: boolean) {
        if(this.isEditMode == editMode) return;

        this.isEditMode = editMode;
        this.editModeEventEmitter.emit(editMode);
    }

    triggerWarningRecalculationChangesEvent(): void {
        this.warningRecalculationChangesEventEmitter.emit();
    }

    triggerStepCallOrderChangeEvent(): void {
        this.stepCallOrderChangeEventEmitter.emit();
    }

    triggerChangeEvent(): void {
        this.changeEventEmitter.emit();
    }

    addStepCall(stepCall: StepCall) {
        this.stepCalls.push(stepCall);
        let stepCallContainer = StepCallTreeUtil.createStepCallContainerWithChildren(stepCall, this.jsonTreeModel, new Map());
        stepCallContainer.jsonTreeNodeState.showChildren = true;
        this.jsonTreeModel.getChildren().push(stepCallContainer);

        this.triggerWarningRecalculationChangesEvent();
        this.triggerChangeEvent();
    }

    removeStepCall(stepCallContainer: StepCallContainerModel) {
        ArrayUtil.removeElementFromArray(stepCallContainer.parentContainer.getChildren(), stepCallContainer);

        this.removeStepCallFromParent(stepCallContainer.stepCall, stepCallContainer.parentContainer);

        this.triggerStepCallOrderChangeEvent();

        this.triggerWarningRecalculationChangesEvent();
        this.triggerChangeEvent();
    }

    private removeStepCallFromParent(stepCallToRemove: StepCall, rootNode: JsonTreeContainer) {

        if (rootNode instanceof JsonTreeModel ) {
            ArrayUtil.removeElementFromArray(this.stepCalls, stepCallToRemove);
            return;
        }

        if (rootNode instanceof SubStepsContainerModel) {
            let parentStepCallContainer: StepCallContainerModel = rootNode.parentContainer as StepCallContainerModel;
            ArrayUtil.removeElementFromArray((parentStepCallContainer.stepCall.stepDef as ComposedStepDef).stepCalls, stepCallToRemove);
        }
    }

    addStepCallToParentContainer(stepCall: StepCall, parentContainer: JsonTreeContainer) {

        if (parentContainer instanceof SubStepsContainerModel) {
            let parentStepCallContainerModel = parentContainer.parentContainer as StepCallContainerModel;
            if (parentStepCallContainerModel.stepCall.stepDef instanceof UndefinedStepDef) {
                let undefinedStepDef: UndefinedStepDef = parentStepCallContainerModel.stepCall.stepDef;
                let newStepDef = new ComposedStepDef();
                newStepDef.phase = undefinedStepDef.phase;
                newStepDef.stepPattern = undefinedStepDef.stepPattern;
                newStepDef.path = undefinedStepDef.path;
                parentStepCallContainerModel.stepCall.stepDef = newStepDef;
            }
        }

        let stepCallContainerModel = StepCallTreeUtil.createStepCallContainerWithChildren(stepCall, parentContainer, new Map());
        this.expandParameters(stepCallContainerModel);

        parentContainer.getChildren().push(
            stepCallContainerModel
        );
        let parentStepCallContainerModel: StepCallContainerModel = this.getFirstParentOfTypeStepCallContainerModel(parentContainer);
        if (parentStepCallContainerModel != null) {
            if(parentStepCallContainerModel.stepCall.stepDef instanceof ComposedStepDef) {
                parentStepCallContainerModel.stepCall.stepDef.stepCalls.push(
                    stepCall
                );
            }
        } else {
            this.stepCalls.push(
                stepCall
            )
        }

        this.triggerWarningRecalculationChangesEvent();
    }

    private expandParameters(stepCallContainerModel: StepCallContainerModel) {
        if (stepCallContainerModel.stepCall.args.length > 0) {
            stepCallContainerModel.jsonTreeNodeState.showChildren = true;
        }
    }

    private getFirstParentOfTypeStepCallContainerModel(parentContainer: JsonTreeContainer): StepCallContainerModel {
        if (parentContainer instanceof StepCallContainerModel) {
            return parentContainer;
        } else {
            if (parentContainer.getParent()) {
                return this.getFirstParentOfTypeStepCallContainerModel(parentContainer.getParent())
            } else {
                return null;
            }
        }
    }

    removeStepCallEditorIfExist() {
        if (this.currentStepCallEditorModel) {
            ArrayUtil.removeElementFromArray(this.currentStepCallEditorModel.parentContainer.getChildren(), this.currentStepCallEditorModel);
            this.currentStepCallEditorModel = null;
        }
    }

    addStepCallEditor(parentContainerModel: JsonTreeContainer) {
        this.removeStepCallEditorIfExist();

        let isRootStepCall = parentContainerModel instanceof JsonTreeModel;

        let stepCallEditorContainerModel = new StepCallEditorContainerModel(
            parentContainerModel,
            parentContainerModel.getChildren().length,
            null,
            isRootStepCall
        );
        parentContainerModel.getChildren().push(
            stepCallEditorContainerModel
        );

        this.currentStepCallEditorModel = stepCallEditorContainerModel;

        this.triggerChangeEvent();
    }

    setSelectedNode(selectedStep: StepCallContainerComponent) {
        this.selectedStep = selectedStep;
    }
    getSelectedNode(): StepCallContainerComponent {
        return this.selectedStep;
    }
}
