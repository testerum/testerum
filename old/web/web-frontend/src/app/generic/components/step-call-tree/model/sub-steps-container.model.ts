import {JsonTreeContainerAbstract} from "../../json-tree/model/json-tree-container.abstract";
import {JsonTreeNodeState} from "../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {JsonTreeContainerOptions} from "../../json-tree/model/behavior/JsonTreeContainerOptions";
import {StepCallContainerModel} from "./step-call-container.model";

export class SubStepsContainerModel extends JsonTreeContainerAbstract {

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    children: Array<StepCallContainerModel> = [];
    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    descendantsHaveWarnings: boolean = false;

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);

        this.getNodeState().showChildren = false;
    }


    getChildren(): Array<StepCallContainerModel> {
        return this.children;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
