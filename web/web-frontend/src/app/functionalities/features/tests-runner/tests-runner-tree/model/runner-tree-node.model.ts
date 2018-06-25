
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeTypeEnum} from "./enums/runner-tree-node-type.enum";
import {StepCall} from "../../../../../model/step-call.model";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {PositionInParent} from "../../../../../model/test/event/fields/position-in-parent.model";
import {ArrayUtil} from "../../../../../utils/array.util";
export class RunnerTreeNodeModel {

    id:string;
    eventKey: EventKey;
    type:RunnerTreeNodeTypeEnum;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;
    text:string;

    parent:RunnerTreeNodeModel;
    children:Array<RunnerTreeNodeModel> = [];

    stepCall:StepCall;

    equals(other: RunnerTreeNodeModel): boolean {
        if(other.id != this.id) {
            return false
        }

        return this.areParentsEquals(other);
    }

    private areParentsEquals(other: RunnerTreeNodeModel): boolean {
        if(other.parent == null && this.parent == null) {
            return true;
        }

        if(this.parent != null && other.parent != null) {
            if(this.parent.id == other.parent.id) {
                return this.areParentsEquals(other.parent)
            }
        }

        return false;
    }

    changeState(newState:ExecutionStatusEnum) {
        this.state = newState;
    }

    findNode(eventKey:EventKey): RunnerTreeNodeModel {
        if(eventKey.positionsFromRoot.length == 0) {
            return null
        }
        return this.findNodeByPositionInParent(ArrayUtil.copyArray(eventKey.positionsFromRoot))
    }

    private findNodeByPositionInParent(positionsInParent: Array<PositionInParent>): RunnerTreeNodeModel {

        positionsInParent.splice(0, 1);
        if(positionsInParent.length == 0) {
            return this;
        }
        let childPositionInparent = positionsInParent[0];
        if(this.children.length < childPositionInparent.indexInParent) {
            return null
        }

        let searchChild = this.children[childPositionInparent.indexInParent];
        if(searchChild.id == childPositionInparent.id) {
            return searchChild.findNodeByPositionInParent(positionsInParent)
        }
        return null;
    }

    hasChild(searchedChild:RunnerTreeNodeModel):boolean {
        for (let child of this.children) {
            if(child == searchedChild) {
                return true;
            }
            let found = child.hasChild(searchedChild);
            if(found) {
                return true;
            }
        }

        return false;
    }

}
