import {ManualUiTreeRootStatusModel} from "../model/manual-ui-tree-root-status.model";
import {ManualTestsStatusTreeRoot} from "../../../model/status-tree/manual-tests-status-tree-root.model";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualUiTreeBaseStatusModel} from "../model/manual-ui-tree-base-status.model";
import {ManualTestsStatusTreeBase} from "../../../model/status-tree/manual-tests-status-tree-base.model";
import {ManualUiTreeContainerStatusModel} from "../model/manual-ui-tree-container-status.model";
import {ManualTestsStatusTreeContainer} from "../../../model/status-tree/manual-tests-status-tree-container.model";
import {ManualTestsStatusTreeNode} from "../../../model/status-tree/manual-tests-status-tree-node.model";
import {ManualUiTreeNodeStatusModel} from "../model/manual-ui-tree-node-status.model";

export class ManualTestsStatusTreeUtil {

    static mapServerModelToTreeModel(manualTestsStatusTreeRoot: ManualTestsStatusTreeRoot, treeModel: JsonTreeModel): JsonTreeModel {

        treeModel.children.length = 0;

        let rootTreeNode = new ManualUiTreeRootStatusModel(treeModel, manualTestsStatusTreeRoot.path, "Manual Test Suite", manualTestsStatusTreeRoot.status);

        ManualTestsStatusTreeUtil.mapServerNodeChildrenToTreeModel(manualTestsStatusTreeRoot, rootTreeNode);

        treeModel.children.push(rootTreeNode);
        return treeModel;
    }

    private static mapServerNodeChildrenToTreeModel(parentServerNode: ManualTestsStatusTreeBase, parentTreeNode: ManualUiTreeBaseStatusModel) {

        let serverNodeChildren: ManualTestsStatusTreeBase[] = null;
        if (parentServerNode instanceof ManualTestsStatusTreeContainer ||
            parentServerNode instanceof ManualTestsStatusTreeRoot) {

            serverNodeChildren = parentServerNode.children;
        }

        if (serverNodeChildren == null) {
            return;
        }
        let parentTreeContainerNode: ManualUiTreeContainerStatusModel = parentTreeNode as ManualUiTreeContainerStatusModel;
        parentTreeContainerNode.getChildren().length = 0;
        for (const serverChildNode of serverNodeChildren) {
            let childTreeNode = this.createTreeNodeFromServerNode(serverChildNode, parentTreeContainerNode);
            parentTreeContainerNode.getChildren().push(childTreeNode);

            this.mapServerNodeChildrenToTreeModel(serverChildNode, childTreeNode);
        }
    }

    private static createTreeNodeFromServerNode(serverNode: ManualTestsStatusTreeBase, parentNode: ManualUiTreeContainerStatusModel): ManualUiTreeBaseStatusModel {
        let treeNode: ManualUiTreeBaseStatusModel;

        if (serverNode instanceof ManualTestsStatusTreeContainer) {
            treeNode = new ManualUiTreeContainerStatusModel(parentNode, serverNode.path, serverNode.name, serverNode.status);
        }

        if (serverNode instanceof ManualTestsStatusTreeNode) {
            treeNode = new ManualUiTreeNodeStatusModel(parentNode, serverNode.path, serverNode.name, serverNode.status);
        }

        if (treeNode == null) {
            throw new Error("Couldn't map the current instance to a tree node ["+JSON.stringify(serverNode)+"]");
        }

        return treeNode;
    }

    static getTreeTestNodes(runnerRootTreeNodeModel: ManualUiTreeRootStatusModel): ManualUiTreeNodeStatusModel[] {
        let result: ManualUiTreeNodeStatusModel[] = [];
        this.addTreeTestsToResultsOfContainer(runnerRootTreeNodeModel, result);
        return result;
    }

    private static addTreeTestsToResultsOfContainer(parentNode: ManualUiTreeContainerStatusModel, result: ManualUiTreeNodeStatusModel[]) {
        for (const childNode of parentNode.getChildren()) {
            if (childNode instanceof ManualUiTreeContainerStatusModel) {
                this.addTreeTestsToResultsOfContainer(childNode, result);
            }

            if (childNode instanceof ManualUiTreeNodeStatusModel) {
                result.push(childNode)
            }
        }
    }
}
