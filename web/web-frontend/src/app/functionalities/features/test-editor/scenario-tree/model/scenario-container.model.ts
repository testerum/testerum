import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeContainerOptions} from "../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {Scenario} from "../../../../../model/test/scenario/scenario.model";

export class ScenarioContainerModel extends JsonTreeContainerAbstract {

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    indexInParent: number = -1;
    children: Array<JsonTreeContainer> = [];

    scenario: Scenario;
    showAsEditScenarioNameMode: boolean = false;

    options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, indexInParent: number, scenario: Scenario) {
        super(parentContainer);
        this.indexInParent = indexInParent;
        this.scenario = scenario;

        this.getNodeState().showChildren = false;

        this.options.displayLines = false;
        this.options.allowDndToOrderChildren = true;
    }

    getChildren(): Array<JsonTreeContainer> {
        return this.children;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
