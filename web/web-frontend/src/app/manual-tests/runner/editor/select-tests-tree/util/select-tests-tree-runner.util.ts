
import {SelectTestTreeRunnerContainerModel} from "../model/select-test-tree-runner-container.model";
import {SelectTestTreeRunnerNodeModel} from "../model/select-test-tree-runner-node.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTestModel} from "../../../../model/manual-test.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../../generic/components/json-tree/util/json-tree-path.util";
import {ManualTestExeModel} from "../../../model/manual-test-exe.model";

export default class SelectTestsTreeRunnerUtil {

    static createRootPackage() {
        let rootPackage: SelectTestTreeRunnerContainerModel = new SelectTestTreeRunnerContainerModel(null,"Test Execution", new Path([], null, null));
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];

        rootPackage.sort();

        return treeModel;
    }

    static mapTestModelToTestJsonTreeModel(treeModel: JsonTreeModel, tests: Array<ManualTestModel>, areNodeSelected: boolean) {
        let rootPackage = treeModel.children[0] as SelectTestTreeRunnerContainerModel;

        for (let test of tests) {
            let testPath = test.path;
            let testContainer: SelectTestTreeRunnerContainerModel = this.getOrCreateTestTreeContainerWithName(testPath, rootPackage);

            if (!testContainer.containsChildNodeWithPath(test.path)) {
                testContainer.children.push(
                    SelectTestsTreeRunnerUtil.getStepTreeNode(testContainer, test, areNodeSelected)
                );
            }
        }
    }

    private static getOrCreateTestTreeContainerWithName(stepPath: Path,
                                                        parentContainer: SelectTestTreeRunnerContainerModel) {
        if (!stepPath) {
            return parentContainer;
        }

        let result: SelectTestTreeRunnerContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = SelectTestsTreeRunnerUtil.getOrCreateContainer(pathDirectory, result)
        }
        return result;
    }

    private static getOrCreateContainer(childContainerName: string, parentContainer: SelectTestTreeRunnerContainerModel) {
        let childContainer: SelectTestTreeRunnerContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children) as SelectTestTreeRunnerContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new SelectTestTreeRunnerContainerModel(parentContainer, childContainerName, childContainerPath);
            childContainer.editable = true;
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private static getStepTreeNode(parentContainer: SelectTestTreeRunnerContainerModel, testModel: ManualTestModel, areNodeSelected: boolean): SelectTestTreeRunnerNodeModel {
        return new SelectTestTreeRunnerNodeModel(
            parentContainer,
            testModel.id,
            testModel.text,
            Path.createInstanceFromPath(testModel.path),
            ManualTestExeModel.createInstanceFrom(testModel),
            areNodeSelected
        );
    }
}
