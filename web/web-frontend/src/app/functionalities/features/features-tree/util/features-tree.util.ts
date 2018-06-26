
import {TestModel} from "../../../../model/test/test.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {FeatureTreeContainerModel} from "../model/feature-tree-container.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {TestTreeNodeModel} from "../model/test-tree-node.model";
import {RootServerTreeNode} from "../../../../model/tree/root-server-tree-node.model";
import {ServerContainerTreeNode} from "../../../../model/tree/server-container-tree-node.model";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {FeatureServerTreeNode} from "../../../../model/tree/feature-server-tree-node.model";
import {TestServerTreeNode} from "../../../../model/tree/test-server-tree-node.model";

export default class FeaturesTreeUtil {

    static mapServerTreeToFeaturesTreeModel(serverRootNode: RootServerTreeNode): JsonTreeModel {

        let rootPackage: FeatureTreeContainerModel = new FeatureTreeContainerModel(null,serverRootNode.name, serverRootNode.path);
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        FeaturesTreeUtil.mapServerNodeChildrenToTreeNode(serverRootNode, rootPackage);

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];

        rootPackage.sort();

        return treeModel;
    }

    private static mapServerNodeChildrenToTreeNode(serverContainerNode: ServerContainerTreeNode, uiContainerNode: FeatureTreeContainerModel) {
        for (const serverNode of serverContainerNode.children) {

            if (serverNode instanceof FeatureServerTreeNode) {
                let uiFeatureNode = new FeatureTreeContainerModel(uiContainerNode, serverNode.name, serverNode.path);
                FeaturesTreeUtil.mapServerNodeChildrenToTreeNode(serverNode, uiFeatureNode);
                uiFeatureNode.editable = true;
                uiContainerNode.children.push(uiFeatureNode);
            }

            if (serverNode instanceof TestServerTreeNode) {
                let uiTestNode = new TestTreeNodeModel(uiContainerNode, serverNode.name, serverNode.path);
                uiContainerNode.children.push(uiTestNode)
            }
        }
    }
}
