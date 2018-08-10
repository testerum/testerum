import {RunnerRootNode} from "../../../../../model/runner/tree/runner-root-node.model";
import {RunnerRootTreeNodeModel} from "../model/runner-root-tree-node.model";
import {RunnerContainerNode} from "../../../../../model/runner/tree/runner-container-node.model";
import {RunnerTreeContainerNodeModel} from "../model/runner-tree-container-node.model";
import {RunnerNode} from "../../../../../model/runner/tree/runner-node.model";
import {RunnerTreeNodeModel} from "../model/runner-tree-node.model";
import {RunnerFeatureNode} from "../../../../../model/runner/tree/runner-feature-node.model";
import {RunnerFeatureTreeNodeModel} from "../model/runner-feature-tree-node.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";

export class RunnerTreeUtil {

    static mapServerModelToTreeModel(runnerRootNode: RunnerRootNode, treeModel:JsonTreeModel): JsonTreeModel {

        treeModel.children.length = 0;

        let rootTreeNode = new RunnerRootTreeNodeModel(treeModel);

        RunnerTreeUtil.mapServerNodeChildrenToTreeModel(runnerRootNode, rootTreeNode);

        treeModel.children.push(rootTreeNode);
        return treeModel;
    }

    private static mapServerNodeChildrenToTreeModel(serverContainerNode: RunnerContainerNode, parentNode: RunnerTreeContainerNodeModel) {

        serverContainerNode.children.length = 0;
        for (const serverChildNode of serverContainerNode.children) {
            let childTreeNode = this.createTreeNodeFromServerNode(serverChildNode, parentNode);
            parentNode.getChildren().push(childTreeNode);
        }
    }

    public static createTreeNodeFromServerNode(serverNode: RunnerNode, parentNode: RunnerTreeContainerNodeModel): RunnerTreeNodeModel {
        let treeNode: RunnerTreeNodeModel;

        if (serverNode instanceof RunnerFeatureNode) {
            let featureTreeNode = new RunnerFeatureTreeNodeModel(parentNode);
            featureTreeNode.text = serverNode.name;

            treeNode = featureTreeNode;
        }

        treeNode.id = serverNode.id;
        treeNode.path = serverNode.path;
        treeNode.eventKey = new EventKey()

        return treeNode;
    }

}
