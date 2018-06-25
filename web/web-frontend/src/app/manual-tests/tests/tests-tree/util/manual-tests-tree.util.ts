
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTestTreeContainerModel} from "../model/manual-test-tree-container.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {ManualTestTreeNodeModel} from "../model/manual-test-tree-node.model";
import {ManualTestModel} from "../../../model/manual-test.model";

export default class ManualTestsTreeUtil {

    static mapTestModelToTestJsonTreeModel(tests: Array<ManualTestModel>): JsonTreeModel {

        let rootPackage: ManualTestTreeContainerModel = new ManualTestTreeContainerModel(null,"Manual Test Suite", new Path([], null, null));
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        for (let test of tests) {
            let testPath = test.path;
            let testContainer: ManualTestTreeContainerModel = this.getOrCreateTestTreeContainerWithName(testPath, rootPackage);

            testContainer.children.push(
                ManualTestsTreeUtil.getStepTreeNode(test, testContainer)
            );
        }

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];

        rootPackage.sort();

        return treeModel;
    }

    private static getOrCreateTestTreeContainerWithName(stepPath: Path,
                                                        parentContainer: ManualTestTreeContainerModel) {
        if (!stepPath) {
            return parentContainer;
        }

        let result: ManualTestTreeContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = ManualTestsTreeUtil.getOrCreateContainer(pathDirectory, result)
        }
        return result;
    }

    private static getOrCreateContainer(childContainerName: string, parentContainer: ManualTestTreeContainerModel) {
        let childContainer: ManualTestTreeContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children) as ManualTestTreeContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new ManualTestTreeContainerModel(parentContainer, childContainerName, childContainerPath);
            childContainer.editable = true;
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private static getStepTreeNode(testModel: ManualTestModel, parentContainer: ManualTestTreeContainerModel): ManualTestTreeNodeModel {
        return new ManualTestTreeNodeModel(testModel.id, testModel.text, Path.createInstanceFromPath(testModel.path), parentContainer);
    }
}
