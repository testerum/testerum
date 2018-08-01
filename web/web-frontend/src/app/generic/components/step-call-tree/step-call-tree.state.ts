import {TreeState} from "../json-tree/model/state/TreeState";
import {EventEmitter} from "@angular/core";
import {StepCallEditorContainerModel} from "./model/step-call-editor-container.model";
import {ArgModalComponent} from "./arg-modal/arg-modal.component";
import {JsonTreeContainer} from "../json-tree/model/json-tree-container.model";
import {StepCall} from "../../../model/step-call.model";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {StepCallContainerModel} from "./model/step-call-container.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {SubStepsContainerModel} from "./model/sub-steps-container.model";
import {StepCallTreeUtil} from "./util/step-call-tree.util";
import {ArrayUtil} from "../../../utils/array.util";

export class StepCallTreeState implements TreeState {
    jsonTreeModel: JsonTreeModel;
    stepCalls: Array<StepCall> = [];

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    stepCallOrderChangeEventEmitter: EventEmitter<void> = new EventEmitter<void>();
    warningRecalculationChangesEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    argModal: ArgModalComponent; //TODO Ionut: remove this, this Modal should be a stand alone compoent as StepChooserModal

    currentStepCallEditorModel: StepCallEditorContainerModel;

    setEditMode(editMode: boolean) {
        this.editModeEventEmitter.emit(editMode);
        this.isEditMode = editMode
    }

    triggerWarningRecalculationChangesEvent(): void {
        this.warningRecalculationChangesEventEmitter.emit();
    }

    triggerStepCallOrderChangeEvent(): void {
        this.stepCallOrderChangeEventEmitter.emit();
    }

    addStepCall(stepCall: StepCall) {
        this.stepCalls.push(stepCall);
        let stepCallContainer = StepCallTreeUtil.createStepCallContainerWithChildren(stepCall, this.jsonTreeModel);
        stepCallContainer.jsonTreeNodeState.showChildren = true;
        this.jsonTreeModel.getChildren().push(stepCallContainer)
    }

    removeStepCall(stepCallContainer: StepCallContainerModel) {
        ArrayUtil.removeElementFromArray(stepCallContainer.parentContainer.getChildren(), stepCallContainer);

        this.removeStepCallFromParent(stepCallContainer.stepCall, stepCallContainer.parentContainer);

        this.triggerStepCallOrderChangeEvent()
    }

    private removeStepCallFromParent(stepCallToRemove: StepCall, rootNode: JsonTreeContainer) {

        if (rootNode instanceof JsonTreeModel) {
            ArrayUtil.removeElementFromArray(this.stepCalls, stepCallToRemove);
            return;
        }

        for (const rootChild of rootNode.getChildren()) {
            if(rootChild instanceof SubStepsContainerModel) {
                this.removeStepCallFromParent(stepCallToRemove, rootChild);
                return;
            }

            if (rootChild instanceof StepCallContainerModel) {
                ArrayUtil.removeElementFromArray((rootChild.stepCall.stepDef as ComposedStepDef).stepCalls, stepCallToRemove);
            }
        }
    }

    addStepCallToParentContainer(stepCall: StepCall, parentContainer: JsonTreeContainer) {
        let stepCallContainerModel = StepCallTreeUtil.createStepCallContainerWithChildren(stepCall, parentContainer);

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
    }
}
