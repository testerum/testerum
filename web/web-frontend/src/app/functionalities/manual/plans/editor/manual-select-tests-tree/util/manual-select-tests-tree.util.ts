import {ManualSelectTestsTreeContainerModel} from "../model/manual-select-tests-tree-container.model";
import {ManualSelectTestsTreeNodeModel} from "../model/manual-select-tests-tree-node.model";
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

        let rootPackage: ManualSelectTestsTreeContainerModel = new ManualSelectTestsTreeContainerModel(treeModel,"Manual Tests To Execute", Path.createInstanceOfEmptyPath());
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        treeModel.children = [rootPackage];

        return treeModel;
    }

    static mapFeaturesWithTestsToTreeModel(treeModel: JsonTreeModel, rootFeatureNode: RootFeatureNode) {
        let rootPackage = treeModel.children[0] as ManualSelectTestsTreeContainerModel;
        rootPackage.children.length = 0;

        ManualSelectTestsTreeUtil.mapFeatureContainerToTreeContainer(rootFeatureNode, rootPackage);

    }

    private static mapFeatureContainerToTreeContainer(containerFeatureNode: ContainerFeatureNode, parentTreeContainer: ManualSelectTestsTreeContainerModel) {
        for (const featureNode of containerFeatureNode.children) {
            if (featureNode instanceof TestFeatureNode) {
                let testNode = new ManualSelectTestsTreeNodeModel(
                    parentTreeContainer,
                    featureNode.name,
                    new Path(featureNode.path.directories, featureNode.path.fileName, "manual_test"),
                    false
                );
                parentTreeContainer.getChildren().push(testNode)
            }
            if (featureNode instanceof FeatureFeatureNode) {
                let dirNode = new ManualSelectTestsTreeContainerModel(parentTreeContainer, featureNode.name, featureNode.path, SelectionStateEnum.NOT_SELECTED);
                parentTreeContainer.getChildren().push(dirNode);

                ManualSelectTestsTreeUtil.mapFeatureContainerToTreeContainer(featureNode, dirNode);
            }
        }
    }
}
