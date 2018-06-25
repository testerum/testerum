import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../../generic/components/json-tree/model/json-tree-node.model";
import {SelectTestTreeRunnerNodeModel} from "./model/select-test-tree-runner-node.model";
import {SelectTestTreeRunnerContainerModel} from "./model/select-test-tree-runner-container.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import SelectTestsTreeRunnerUtil from "./util/select-tests-tree-runner.util";
import {ManualTestModel} from "../../../model/manual-test.model";
import {ManualTestsService} from "../../../tests/service/manual-tests.service";
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {JsonTreePathContainer} from "../../../../generic/components/json-tree/model/path/json-tree-path-container.model";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualTestsRunner} from "../../model/manual-tests-runner.model";
import {ManualTestExeModel} from "../../model/manual-test-exe.model";

@Injectable()
export class SelectTestsTreeRunnerService {

    isEditMode: boolean = false;

    testsJsonTreeModel: JsonTreeModel;

    manualTestRunner: ManualTestsRunner;

    constructor(private manualTestsService: ManualTestsService){
    }

    initializeTestsTree(manualTestRunner: ManualTestsRunner) {
        this.manualTestRunner = manualTestRunner;

        this.manualTestsService.getTests().subscribe(
            testModels => this.setTestModels(testModels)
        );
    }

    setTestModels(testModels: Array<ManualTestModel>): void {

        let jsonTreeModel = SelectTestsTreeRunnerUtil.createRootPackage();
        SelectTestsTreeRunnerUtil.mapTestModelToTestJsonTreeModel(jsonTreeModel, this.manualTestRunner.testsToExecute, true);
        SelectTestsTreeRunnerUtil.mapTestModelToTestJsonTreeModel(jsonTreeModel, testModels, false);
        this.testsJsonTreeModel =  jsonTreeModel;

        this.sort();

        this.calculateSelectionOfDirectories(jsonTreeModel.children[0] as SelectTestTreeRunnerContainerModel)
    }

    private calculateSelectionOfDirectories(rootNode: SelectTestTreeRunnerContainerModel) {
        for (const child of rootNode.children) {
            if(child.isContainer()) {
                this.calculateSelectionOfDirectories(child as SelectTestTreeRunnerContainerModel)
            }
            this.calculateSelectionForNode(child)
        }
    }

    private calculateSelectionForNode(node: SelectTestTreeRunnerNodeModel) {
        let parentNodes: Array<SelectTestTreeRunnerContainerModel> = this.getParentNodesOf(
            node
        ) as Array<SelectTestTreeRunnerContainerModel>;
        for (const parentNode of parentNodes) {
            parentNode.calculateCheckState()
        }
    }

    sort(): void {
        this.sortChildren(this.testsJsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: SelectTestTreeRunnerNodeModel, right: SelectTestTreeRunnerNodeModel) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            if (left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if (left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });

        children.forEach( it => {
            if(it.isContainer()) {
                this.sortChildren((it as SelectTestTreeRunnerContainerModel).children)
            }
        })
    }

    getParentNodesOf(node: JsonTreePathNode): Array<JsonTreePathContainer> {
        return this.getParentNodesOfPath(
            this.testsJsonTreeModel.children as Array<JsonTreePathNode>,
            node.path,
            []
        );
    }

    private getParentNodesOfPath(nodesToSearched: Array<JsonTreePathNode>, searchedPath: Path, result: Array<JsonTreePathContainer>): Array<JsonTreePathContainer> {
        for (let childPathNode of nodesToSearched) {
            if(childPathNode.path.equals(searchedPath)) {
                return result
            }

            if (searchedPath.hasPrefix(childPathNode.path) && childPathNode.isContainer()) {
                result.unshift(childPathNode as JsonTreePathContainer);
                return this.getParentNodesOfPath(
                    (childPathNode as JsonTreePathContainer).getChildren() as Array<JsonTreePathNode>,
                    searchedPath,
                    result
                )
            }
        }
        return [];
    }

    getSelectedTests(): Array<ManualTestExeModel> {
        let result: Array<ManualTestExeModel> = [];
        let treeLeafs: Array<SelectTestTreeRunnerNodeModel> = [];
        this.getTreeLeafs(this.testsJsonTreeModel.children, treeLeafs);

        for (const treeLeaf of treeLeafs) {
            if (treeLeaf.isSelected) {
                result.push(treeLeaf.payload)
            }
        }
        return result;
    }

    private getTreeLeafs(treeNodes: Array<JsonTreeNode>, result:Array<SelectTestTreeRunnerNodeModel>) {
        for (const treeNode of treeNodes) {
            if (treeNode.isContainer()) {
                this.getTreeLeafs(
                    (treeNode as JsonTreeContainer).getChildren(),
                    result
                )
            }
            result.push(treeNode as SelectTestTreeRunnerNodeModel)
        }
        return result;
    }
}
