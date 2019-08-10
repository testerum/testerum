import {ScenarioParamNodeModel} from "./scenario-param-node.model";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeContainerOptions} from "../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class ScenarioParamsContainerModel extends JsonTreeContainerAbstract {

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    children: Array<ScenarioParamNodeModel> = [];
    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);

        this.getNodeState().showChildren = true;
    }

    getChildren(): Array<ScenarioParamNodeModel> {
        return this.children;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
