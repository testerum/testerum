import {SelectTestsTreeContainerModel} from "../model/select-tests-tree-container.model";
import {SelectTestsTreeNodeModel} from "../model/select-tests-tree-node.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {RootFeatureNode} from "../../../../../../model/feature/tree/root-feature-node.model";
import {ContainerFeatureNode} from "../../../../../../model/feature/tree/container-feature-node.model";
import {TestFeatureNode} from "../../../../../../model/feature/tree/test-feature-node.model";
import {FeatureFeatureNode} from "../../../../../../model/feature/tree/feature-feature-node.model";
import {SelectionStateEnum} from "../model/enum/selection-state.enum";

export default class ManualSelectTestsTreeUtil {

    static createRootPackage() {
        let treeModel: JsonTreeModel = new JsonTreeModel();

        let rootPackage: SelectTestsTreeContainerModel = new SelectTestsTreeContainerModel(treeModel,"Test Execution", Path.createInstanceOfEmptyPath());
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        treeModel.children = [rootPackage];

        return treeModel;
    }

    static mapFeaturesWithTestsToTreeModel(treeModel: JsonTreeModel, rootFeatureNode: RootFeatureNode) {
        let rootPackage = treeModel.children[0] as SelectTestsTreeContainerModel;
        ManualSelectTestsTreeUtil.mapFeatureContainerToTreeContainer(rootFeatureNode, rootPackage);

    }

    private static mapFeatureContainerToTreeContainer(containerFeatureNode: ContainerFeatureNode, parentTreeContainer: SelectTestsTreeContainerModel) {
        for (const featureNode of containerFeatureNode.children) {
            if (featureNode instanceof TestFeatureNode) {
                let testNode = new SelectTestsTreeNodeModel(parentTreeContainer, featureNode.name, featureNode.path, false);
                parentTreeContainer.getChildren().push(testNode)
            }
            if (featureNode instanceof FeatureFeatureNode) {
                let dirNode = new SelectTestsTreeContainerModel(parentTreeContainer, featureNode.name, featureNode.path, SelectionStateEnum.NOT_SELECTED);
                parentTreeContainer.getChildren().push(dirNode);

                ManualSelectTestsTreeUtil.mapFeatureContainerToTreeContainer(featureNode, dirNode);
            }
        }
    }
}
