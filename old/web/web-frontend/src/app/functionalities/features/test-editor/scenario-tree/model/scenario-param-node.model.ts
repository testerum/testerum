import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ScenarioParam} from "../../../../../model/test/scenario/param/scenario-param.model";

export class ScenarioParamNodeModel extends JsonTreeNodeAbstract {

    parentContainer: JsonTreeContainer;
    scenarioParam: ScenarioParam;

    constructor(parentContainer: JsonTreeContainer, scenarioParam: ScenarioParam) {
        super(parentContainer);

        this.scenarioParam = scenarioParam;
    }
}
