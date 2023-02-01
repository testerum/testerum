import { JsonTreeModel } from "../../../../generic/components/json-tree/model/json-tree.model";
import { FeatureTreeContainerModel } from "../model/feature-tree-container.model";
import { TestTreeNodeModel } from "../model/test-tree-node.model";
import { RootFeatureNode } from "../../../../model/feature/tree/root-feature-node.model";
import { ContainerFeatureNode } from "../../../../model/feature/tree/container-feature-node.model";
import { FeatureFeatureNode } from "../../../../model/feature/tree/feature-feature-node.model";
import { TestFeatureNode } from "../../../../model/feature/tree/test-feature-node.model";

export default class FeaturesTreeUtil {

    static mapServerTreeToFeaturesTreeModel(serverRootNode: RootFeatureNode): JsonTreeModel {

        let rootPackage: FeatureTreeContainerModel = new FeatureTreeContainerModel(
            null,
            serverRootNode.name,
            serverRootNode.path,
            serverRootNode.hasOwnOrDescendantWarnings,
            serverRootNode.hasHooks
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

    private static mapServerNodeChildrenToTreeNode(serverContainerNode: ContainerFeatureNode, uiContainerNode: FeatureTreeContainerModel) {
        for (const serverNode of serverContainerNode.children) {

            if (serverNode instanceof FeatureFeatureNode) {
                let uiFeatureNode = new FeatureTreeContainerModel(
                    uiContainerNode,
                    serverNode.name,
                    serverNode.path,
                    serverNode.hasOwnOrDescendantWarnings,
                    serverNode.hasHooks
                );

                FeaturesTreeUtil.mapServerNodeChildrenToTreeNode(serverNode, uiFeatureNode);
                uiFeatureNode.editable = true;
                uiContainerNode.children.push(uiFeatureNode);
            }

            if (serverNode instanceof TestFeatureNode) {
                let uiTestNode = new TestTreeNodeModel(uiContainerNode, serverNode.name, serverNode.path, serverNode.properties, serverNode.hasOwnOrDescendantWarnings);
                uiContainerNode.children.push(uiTestNode)
            }
        }
    }
}
