import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTestExeModel} from "../../../runner/model/manual-test-exe.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {ManualTestsTreeExecutorNodeModel} from "../model/manual-tests-tree-executor-node.model";
import {ManualTestsTreeExecutorContainerModel} from "../model/manual-tests-tree-executor-container.model";
import {ManualTestStatus} from "../../../model/enums/manual-test-status.enum";


export default class ManualTestsTreeExecutorUtil {

    static createRootPackage() {
        let rootPackage: ManualTestsTreeExecutorContainerModel = new ManualTestsTreeExecutorContainerModel(
            "Test Execution",
            new Path([], null, null),
            ManualTestStatus.NOT_EXECUTED,
            null
        );
        rootPackage.isRootPackage = true;
        rootPackage.editable = false;

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];

        rootPackage.sort();

        return treeModel;
    }

    static mapTestModelToTestJsonTreeModel(treeModel: JsonTreeModel, tests: Array<ManualTestExeModel>, areNodeSelected: boolean) {
        let rootPackage = treeModel.children[0] as ManualTestsTreeExecutorContainerModel;

        for (let test of tests) {
            let testPath = test.path;
            let testContainer: ManualTestsTreeExecutorContainerModel = this.getOrCreateTestTreeContainerWithName(testPath, rootPackage);

            testContainer.children.push(
                ManualTestsTreeExecutorUtil.getStepTreeNode(test, areNodeSelected, testContainer)
            );
        }
    }

    private static getOrCreateTestTreeContainerWithName(stepPath: Path,
                                                        parentContainer: ManualTestsTreeExecutorContainerModel) {
        if (!stepPath) {
            return parentContainer;
        }

        let result: ManualTestsTreeExecutorContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = ManualTestsTreeExecutorUtil.getOrCreateContainer(pathDirectory, result)
        }
        return result;
    }

    private static getOrCreateContainer(childContainerName: string, parentContainer: ManualTestsTreeExecutorContainerModel) {
        let childContainer: ManualTestsTreeExecutorContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children) as ManualTestsTreeExecutorContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new ManualTestsTreeExecutorContainerModel(childContainerName, childContainerPath, ManualTestStatus.NOT_EXECUTED, parentContainer);
            childContainer.editable = true;
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private static getStepTreeNode(testModel: ManualTestExeModel, areNodeSelected: boolean, parentContainer: ManualTestsTreeExecutorContainerModel): ManualTestsTreeExecutorNodeModel {
        return new ManualTestsTreeExecutorNodeModel(
            testModel.id,
            testModel.text,
            Path.createInstanceFromPath(testModel.path),
            testModel,
            areNodeSelected,
            parentContainer
        );
    }
}
