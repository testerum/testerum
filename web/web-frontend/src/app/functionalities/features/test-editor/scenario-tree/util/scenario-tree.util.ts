import {Scenario} from "../../../../../model/test/scenario/scenario.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ScenarioContainerModel} from "../model/scenario-container.model";
import {ScenarioParamsContainerModel} from "../model/scenario-params-container.model";
import {ScenarioParam} from "../../../../../model/test/scenario/param/scenario-param.model";
import {ScenarioParamNodeModel} from "../model/scenario-param-node.model";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";

export class ScenarioTreeUtil {

    static mapScenarioToTreeModel(scenarios: Scenario[], treeModel: JsonTreeModel) {

        let treeChildren: ScenarioContainerModel[] = [];
        for (let i = 0; i < scenarios.length; i++) {
            treeChildren.push(
                this.getScenarioContainer(scenarios[i], i, treeModel)
            );
        }
        this.copyChildrenStateFromOldToNew(treeModel.children, treeChildren);
        treeModel.children = treeChildren;
    }

    static getScenarioContainer(scenario: Scenario, indexInParent: number, parent: JsonTreeModel): ScenarioContainerModel {
        let scenarioContainerModel = new ScenarioContainerModel(parent, indexInParent, scenario);
        let scenarioParamsContainerModel = new ScenarioParamsContainerModel(scenarioContainerModel);
        scenarioParamsContainerModel.jsonTreeNodeState.showChildren = true;

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

    static getScenarioParamNode(parent: ScenarioParamsContainerModel, scenarioParam: ScenarioParam): ScenarioParamNodeModel {
        return new ScenarioParamNodeModel(parent, scenarioParam);
    }

    static getAllScenariosName(scenarios: Scenario[]): string[] {
        let result = [];
        for (const scenario of scenarios) {
            if (scenario.name) {
                result.push(scenario.name)
            }
        }
        return result;
    }


    private static copyChildrenStateFromOldToNew(oldChildren: Array<JsonTreeNode>, newChildren: Array<JsonTreeNode>) {
        if(oldChildren == null || oldChildren.length == 0 || newChildren == null || newChildren.length == 0) return;
        if(oldChildren.length != newChildren.length) return;

        for (let i = 0; i < oldChildren.length; i++) {
            let oldChild = oldChildren[i];
            let newChild = newChildren[i];

            if (oldChild.isContainer() && newChild.isContainer()) {
                this.copyTreeContainerStateFromOldToNew(oldChild as JsonTreeContainer, newChild as JsonTreeContainer);
            }
        }
    }

    private static copyTreeContainerStateFromOldToNew(oldChild: JsonTreeContainer, newChild: JsonTreeContainer) {
        newChild.getNodeState().showChildren = oldChild.getNodeState().showChildren;
        this.copyChildrenStateFromOldToNew(oldChild.getChildren(), newChild.getChildren());
    }
}
