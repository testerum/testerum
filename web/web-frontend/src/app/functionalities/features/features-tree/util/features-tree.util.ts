import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {FeatureTreeContainerModel} from "../model/feature-tree-container.model";
import {TestTreeNodeModel} from "../model/test-tree-node.model";
import {ServerRootMainNode} from "../../../../model/main_tree/server-root-main-node.model";
import {ServerContainerMainNode} from "../../../../model/main_tree/server-container-main-node.model";
import {ServerFeatureMainNode} from "../../../../model/main_tree/server-feature-main-node.model";
import {ServerTestMainNode} from "../../../../model/main_tree/server-test-main-node.model";

export default class FeaturesTreeUtil {

    static mapServerTreeToFeaturesTreeModel(serverRootNode: ServerRootMainNode): JsonTreeModel {

        let rootPackage: FeatureTreeContainerModel = new FeatureTreeContainerModel(
            null,
            serverRootNode.name,
            serverRootNode.path,
            serverRootNode.hasOwnOrDescendantWarnings
        );

        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        FeaturesTreeUtil.mapServerNodeChildrenToTreeNode(serverRootNode, rootPackage);

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];
        rootPackage.parentContainer = treeModel;

        rootPackage.sort();

        return treeModel;
    }

    private static mapServerNodeChildrenToTreeNode(serverContainerNode: ServerContainerMainNode, uiContainerNode: FeatureTreeContainerModel) {
        for (const serverNode of serverContainerNode.children) {

            if (serverNode instanceof ServerFeatureMainNode) {
                let uiFeatureNode = new FeatureTreeContainerModel(uiContainerNode, serverNode.name, serverNode.path, serverNode.hasOwnOrDescendantWarnings);
                FeaturesTreeUtil.mapServerNodeChildrenToTreeNode(serverNode, uiFeatureNode);
                uiFeatureNode.editable = true;
                uiContainerNode.children.push(uiFeatureNode);
            }

            if (serverNode instanceof ServerTestMainNode) {
                let uiTestNode = new TestTreeNodeModel(uiContainerNode, serverNode.name, serverNode.path, serverNode.properties, serverNode.hasOwnOrDescendantWarnings);
                uiContainerNode.children.push(uiTestNode)
            }
        }
    }
}
