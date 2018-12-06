import {JsonTreeContainerAbstract} from "../../json-tree/model/json-tree-container.abstract";
import {JsonTreeNodeState} from "../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {StepCall} from "../../../../model/step-call.model";
import {JsonTreeContainerOptions} from "../../json-tree/model/behavior/JsonTreeContainerOptions";
import {SubStepsContainerModel} from "./sub-steps-container.model";

export class StepCallContainerModel extends JsonTreeContainerAbstract {

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    indexInParent: number = -1;
    children: Array<JsonTreeContainer> = [];

    stepCall: StepCall;
    isRootStepCall = false;

    options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, indexInParent:number, stepCall: StepCall, isRootStepCall: boolean = false) {
        super(parentContainer);
        this.indexInParent = indexInParent;
        this.stepCall = stepCall;
        this.isRootStepCall = isRootStepCall;

        this.getNodeState().showChildren = false;

        if (isRootStepCall) {
            this.options.displayLines = false;
            this.options.allowDndToOrderChildren = true;
        }
    }

    getChildren(): Array<JsonTreeContainer> {
        return this.children;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }

    getSubStepsContainerModel(): SubStepsContainerModel {
        for (const child of this.children) {
            if (child instanceof SubStepsContainerModel) {
                return child;
            }
        }

        return null;
    }
}
