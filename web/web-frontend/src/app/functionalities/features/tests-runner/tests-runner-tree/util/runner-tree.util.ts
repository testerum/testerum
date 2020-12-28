import {RunnerRootNode} from "../../../../../model/runner/tree/runner-root-node.model";
import {RunnerRootTreeNodeModel} from "../model/runner-root-tree-node.model";
import {RunnerTreeContainerNodeModel} from "../model/runner-tree-container-node.model";
import {RunnerNode} from "../../../../../model/runner/tree/runner-node.model";
import {RunnerTreeNodeModel} from "../model/runner-tree-node.model";
import {RunnerFeatureNode} from "../../../../../model/runner/tree/runner-feature-node.model";
import {RunnerFeatureTreeNodeModel} from "../model/runner-feature-tree-node.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerTestNode} from "../../../../../model/runner/tree/runner-test-node.model";
import {RunnerComposedStepNode} from "../../../../../model/runner/tree/runner-composed-step-node.model";
import {RunnerTestTreeNodeModel} from "../model/runner-test-tree-node.model";
import {RunnerComposedStepTreeNodeModel} from "../model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "../model/runner-basic-step-tree-node.model";
import {RunnerBasicStepNode} from "../../../../../model/runner/tree/runner-basic-step-node.model";
import {RunnerUndefinedStepNode} from "../../../../../model/runner/tree/runner-undefined-step-node.model";
import {RunnerParametrizedTestNode} from "../../../../../model/runner/tree/runner-parametrized-test.node";
import {RunnerScenarioNode} from "../../../../../model/runner/tree/runner-scenario-node.model";
import {RunnerParametrizedTestTreeNodeModel} from "../model/runner-parametrized-test-tree-node.model";
import {RunnerScenarioTreeNodeModel} from "../model/runner-scenario-tree-node.model";

export class RunnerTreeUtil {

    static mapServerModelToTreeModel(runnerRootNode: RunnerRootNode, treeModel: JsonTreeModel): JsonTreeModel {

        treeModel.children.length = 0;

        let rootTreeNode = new RunnerRootTreeNodeModel(treeModel);
        rootTreeNode.id = "TestSuite";

        RunnerTreeUtil.mapServerNodeChildrenToTreeModel(runnerRootNode, rootTreeNode);

        treeModel.children.push(rootTreeNode);
        return treeModel;
    }

    private static mapServerNodeChildrenToTreeModel(parentServerNode: RunnerNode, parentTreeNode: RunnerTreeNodeModel) {

        let serverNodeChildren: RunnerNode[] = null;
        if (parentServerNode instanceof RunnerRootNode ||
            parentServerNode instanceof RunnerFeatureNode ||
            parentServerNode instanceof RunnerTestNode ||
            parentServerNode instanceof RunnerParametrizedTestNode ||
            parentServerNode instanceof RunnerScenarioNode ||
            parentServerNode instanceof RunnerComposedStepNode) {
            serverNodeChildren = parentServerNode.children;
        }

        if (serverNodeChildren == null) {
            return;
        }
        let parentTreeContainerNode: RunnerTreeContainerNodeModel = parentTreeNode as RunnerTreeContainerNodeModel;
        parentTreeContainerNode.getChildren().length = 0;
        for (const serverChildNode of serverNodeChildren) {
            let childTreeNode = this.createTreeNodeFromServerNode(serverChildNode, parentTreeContainerNode);
            parentTreeContainerNode.getChildren().push(childTreeNode);

            this.mapServerNodeChildrenToTreeModel(serverChildNode, childTreeNode);
        }
    }

    private static createTreeNodeFromServerNode(serverNode: RunnerNode, parentNode: RunnerTreeContainerNodeModel): RunnerTreeNodeModel {
        let treeNode: RunnerTreeNodeModel;

        if (serverNode instanceof RunnerFeatureNode) {
            let featureTreeNode = new RunnerFeatureTreeNodeModel(parentNode);
            featureTreeNode.text = serverNode.name;

            treeNode = featureTreeNode;
        }

        if (serverNode instanceof RunnerTestNode) {
            let testTreeNode = new RunnerTestTreeNodeModel(parentNode);
            testTreeNode.text = serverNode.name;
            testTreeNode.getNodeState().showChildren = false;
            testTreeNode.enabled = serverNode.enabled;

            treeNode = testTreeNode;
        }

        if (serverNode instanceof RunnerParametrizedTestNode) {
            let parametrizedTestTreeNode = new RunnerParametrizedTestTreeNodeModel(parentNode);
            parametrizedTestTreeNode.text = serverNode.name;
            parametrizedTestTreeNode.getNodeState().showChildren = true;

            treeNode = parametrizedTestTreeNode;
        }

        if (serverNode instanceof RunnerScenarioNode) {
            let scenarioTreeNodeModel = new RunnerScenarioTreeNodeModel(parentNode);
            scenarioTreeNodeModel.scenarioIndex = serverNode.scenarioIndex;
            scenarioTreeNodeModel.text = serverNode.name;
            scenarioTreeNodeModel.getNodeState().showChildren = false;
            scenarioTreeNodeModel.enabled = serverNode.enabled;

            treeNode = scenarioTreeNodeModel;
        }

        if (serverNode instanceof RunnerComposedStepNode) {
            let composedStepTreeNode = new RunnerComposedStepTreeNodeModel(parentNode);
            composedStepTreeNode.stepCall = serverNode.stepCall;
            composedStepTreeNode.getNodeState().showChildren = false;

            treeNode = composedStepTreeNode;
        }

        if (serverNode instanceof RunnerBasicStepNode || serverNode instanceof RunnerUndefinedStepNode) {
            let basicStepTreeNode = new RunnerBasicStepTreeNodeModel(parentNode);
            basicStepTreeNode.stepCall = serverNode.stepCall;

            treeNode = basicStepTreeNode;
        }

        if (treeNode == null) {
            throw new Error("Couldn't map the current instance to a tree node [" + JSON.stringify(serverNode) + "]");
        }

        treeNode.id = serverNode.id;
        treeNode.path = serverNode.path;

        return treeNode;
    }

    static getTreeTestNodes(runnerRootTreeNodeModel: RunnerRootTreeNodeModel): RunnerTestTreeNodeModel[] {
        let result: RunnerTestTreeNodeModel[] = [];
        this.addTreeTestsToResultsOfContainer(runnerRootTreeNodeModel, result);
        return result;
    }

    private static addTreeTestsToResultsOfContainer(parentNode: RunnerTreeContainerNodeModel, result: RunnerTestTreeNodeModel[]) {
        for (const childNode of parentNode.getChildren()) {
            if (childNode instanceof RunnerFeatureTreeNodeModel) {
                this.addTreeTestsToResultsOfContainer(childNode, result);
            }

            if (childNode instanceof RunnerTestTreeNodeModel) {
                result.push(childNode)
            }
        }
    }

    static getAllNodesMapByEventKey(treeRootNode: RunnerRootTreeNodeModel): Map<string, RunnerTreeNodeModel> {
        let result = new Map<string, RunnerTreeNodeModel>();
        this.getAllNodesMapByEventKeyOfContainer(treeRootNode, result);
        return result;
    }

    private static getAllNodesMapByEventKeyOfContainer(parentNode: RunnerTreeContainerNodeModel, result: Map<string, RunnerTreeNodeModel>) {
        for (const childNode of parentNode.getChildren()) {
            if (childNode instanceof RunnerTreeContainerNodeModel) {
                this.getAllNodesMapByEventKeyOfContainer(childNode, result);
            }

            let eventKey = this.getNodeFullEventKey(childNode);
            result.set(eventKey, childNode);
        }
    }

    private static getNodeFullEventKey(node: RunnerTreeNodeModel) {
        let eventKey = "";

        let currentNode: RunnerTreeNodeModel = node;
        while (!(currentNode instanceof JsonTreeModel)) {
            eventKey = this.getNodeEventKey(currentNode) + "#" + eventKey;
            currentNode = currentNode.getParent() as RunnerTreeContainerNodeModel;
        }

        return eventKey;
    }

    private static getNodeEventKey(node: RunnerTreeNodeModel): string {
        return node.id + "_" + node.parentContainer.getChildren().indexOf(node, 0);
    }

    static getNumberOfSimpleTestsAndScenarios(treeRootNode: RunnerRootTreeNodeModel): number {
        return this.getNumberOfSimpleTestsAnsScenariosOfContainer(treeRootNode, 0);
    }

    private static getNumberOfSimpleTestsAnsScenariosOfContainer(parentNode: RunnerTreeContainerNodeModel, result: number): number {
        for (const childNode of parentNode.getChildren()) {
            if (childNode instanceof RunnerFeatureTreeNodeModel || childNode instanceof RunnerParametrizedTestTreeNodeModel) {
                result = this.getNumberOfSimpleTestsAnsScenariosOfContainer(childNode, result);
            }

            if (childNode instanceof RunnerTestTreeNodeModel || childNode instanceof RunnerScenarioTreeNodeModel) {
                result++;
            }
        }
        return result;
    }
}
