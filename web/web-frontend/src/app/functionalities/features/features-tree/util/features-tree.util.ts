
import {TestModel} from "../../../../model/test/test.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {FeatureTreeContainerModel} from "../model/feature-tree-container.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {TestTreeNodeModel} from "../model/test-tree-node.model";

export default class FeaturesTreeUtil {

    static mapTestModelToTestJsonTreeModel(tests: Array<TestModel>): JsonTreeModel {

        let rootPackage: FeatureTreeContainerModel = new FeatureTreeContainerModel(null,"Features", new Path([], null, null));
        rootPackage.id = "Features";
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        for (let test of tests) {
            let testPath = test.path;
            let testContainer: FeatureTreeContainerModel = this.getOrCreateTestTreeContainerWithName(testPath, rootPackage);

            testContainer.children.push(
                FeaturesTreeUtil.getStepTreeNode(testContainer, test)
            );
        }

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];

        rootPackage.sort();

        return treeModel;
    }

    private static getOrCreateTestTreeContainerWithName(stepPath: Path,
                                                        parentContainer: FeatureTreeContainerModel) {
        if (!stepPath) {
            return parentContainer;
        }

        let result: FeatureTreeContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = FeaturesTreeUtil.getOrCreateContainer(pathDirectory, result)
        }
        return result;
    }

    private static getOrCreateContainer(childContainerName: string, parentContainer: FeatureTreeContainerModel) {
        let childContainer: FeatureTreeContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children) as FeatureTreeContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new FeatureTreeContainerModel(parentContainer, childContainerName, childContainerPath);
            childContainer.editable = true;
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private static getStepTreeNode(parentContainer: FeatureTreeContainerModel, testModel: TestModel): TestTreeNodeModel {
        return new TestTreeNodeModel(
            parentContainer,
            testModel.id,
            testModel.text,
            Path.createInstanceFromPath(testModel.path)
        );
    }
}
