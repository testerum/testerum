import {Scenario} from "../../../../../model/test/scenario/scenario.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ScenarioContainerModel} from "../model/scenario-container.model";
import {ScenarioParamsContainerModel} from "../model/scenario-params-container.model";
import {ScenarioParam} from "../../../../../model/test/scenario/param/scenario-param.model";
import {ScenarioParamNodeModel} from "../model/scenario-param-node.model";

export class ScenarioTreeUtil {

    static mapScenarioToTreeModel(scenarios: Scenario[], treeModel: JsonTreeModel) {
        for (let i = 0; i < scenarios.length; i++) {
            treeModel.children.push(
                this.getScenarioContainer(scenarios[i], i, treeModel)
            )
        }
    }

    static getScenarioContainer(scenario: Scenario, indexInParent: number, parent: JsonTreeModel): ScenarioContainerModel {
        let scenarioContainerModel = new ScenarioContainerModel(parent, indexInParent, scenario);
        let scenarioParamsContainerModel = new ScenarioParamsContainerModel(scenarioContainerModel);

        for (const param of scenario.params) {
            scenarioParamsContainerModel.children.push(
                this.getScenarioParamNode(scenarioParamsContainerModel, param)
            )
        }

        scenarioContainerModel.children.push(
            scenarioParamsContainerModel
        );
        return scenarioContainerModel;
    }

    private static getScenarioParamNode(parent: ScenarioParamsContainerModel, scenarioParam: ScenarioParam): ScenarioParamNodeModel {
        return new ScenarioParamNodeModel(parent, scenarioParam);
    }
}
