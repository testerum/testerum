import {EventEmitter, Injectable} from "@angular/core";
import {ArgModalComponent} from "./arg-modal/arg-modal.component";
import {StepCall} from "../../../model/step-call.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {StepCallTreeUtil} from "./util/step-call-tree.util";

@Injectable()
export class StepCallTreeService {
    stepCalls: Array<StepCall>;
    jsonTreeModel: JsonTreeModel = new JsonTreeModel();

    isEditMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    stepCallOrderChangeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    argModal: ArgModalComponent;

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
}
