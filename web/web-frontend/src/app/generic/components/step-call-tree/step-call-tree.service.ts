import {EventEmitter, Injectable} from "@angular/core";
import {ArgModalComponent} from "./arg-modal/arg-modal.component";
import {StepCall} from "../../../model/step-call.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {StepCallTreeUtil} from "./util/step-call-tree.util";
import {ArrayUtil} from "../../../utils/array.util";
import {StepCallContainerModel} from "./model/step-call-container.model";
import {JsonTreeContainer} from "../json-tree/model/json-tree-container.model";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {SubStepsContainerModel} from "./model/sub-steps-container.model";
import {StepCallEditorContainerModel} from "./model/step-call-editor-container.model";

@Injectable()
export class StepCallTreeService {
    stepCalls: Array<StepCall>;
    jsonTreeModel: JsonTreeModel = new JsonTreeModel();

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    stepCallOrderChangeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    argModal: ArgModalComponent;

    currentStepCallEditorModel: StepCallEditorContainerModel;

    setEditMode(editMode: boolean) {
        this.editModeEventEmitter.emit(editMode);
        this.isEditMode = editMode
    }

    triggerStepCallOrderChangeEvent(): void {
        this.stepCallOrderChangeEventEmitter.emit();
    }

    initTreeModel(stepCalls: Array<StepCall>) {
        this.stepCalls = stepCalls;
        StepCallTreeUtil.mapStepCallsToJsonTreeModel(this.stepCalls, this.jsonTreeModel)
    }

    addStepCall(stepCall: StepCall) {
        this.stepCalls.push(stepCall);
        let stepCallContainer = StepCallTreeUtil.createStepCallContainerWithChildren(stepCall, this.jsonTreeModel);
        stepCallContainer.jsonTreeNodeState.showChildren = true;
        this.jsonTreeModel.getChildren().push(stepCallContainer)
    }

    removeStepCall(stepCallContainer: StepCallContainerModel) {
        this.removeStepCallContainerFromRoot(stepCallContainer, this.jsonTreeModel);

        this.triggerStepCallOrderChangeEvent()
    }

    private removeStepCallContainerFromRoot(stepCallContainerToRemove: StepCallContainerModel, rootNode: JsonTreeContainer) {
        for (const rootChild of rootNode.getChildren()) {
            if (rootChild == stepCallContainerToRemove) {
                if (rootChild.isContainer()) {
                    this.removeStepCallFromParent(rootChild as JsonTreeContainer, stepCallContainerToRemove.stepCall);
                }
                ArrayUtil.removeElementFromArray(rootNode.getChildren(), stepCallContainerToRemove);
                return;
            }

            if(rootChild instanceof StepCallContainerModel ||
                rootChild instanceof SubStepsContainerModel) {
                this.removeStepCallContainerFromRoot(stepCallContainerToRemove, rootChild)
            }
        }
    }

    private removeStepCallFromParent(treeContainer: JsonTreeContainer, stepCallToRemove: StepCall) {
        if (treeContainer instanceof StepCallContainerModel) {
            if (treeContainer.stepCall.stepDef instanceof ComposedStepDef) {
                ArrayUtil.removeElementFromArray(treeContainer.stepCall.stepDef.stepCalls, stepCallToRemove);
            }
        } else {
            this.removeStepCallFromParent(treeContainer.getParent(), stepCallToRemove);
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
