import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {SelectTestsTreeNodeModel} from "./model/select-tests-tree-node.model";
import {SelectTestsTreeContainerModel} from "./model/select-tests-tree-container.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import ManualSelectTestsTreeUtil from "./util/manual-select-tests-tree.util";
import {ManualTestsRunner} from "../../../../../manual-tests/runner/model/manual-tests-runner.model";
import {ManualTestsService} from "../../../../../manual-tests/tests/service/manual-tests.service";
import {ManualTestModel} from "../../../../../manual-tests/model/manual-test.model";
import {JsonTreePathNode} from "../../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {JsonTreePathContainer} from "../../../../../generic/components/json-tree/model/path/json-tree-path-container.model";
import {ManualTestExeModel} from "../../../../../manual-tests/runner/model/manual-test-exe.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";

@Injectable()
export class ManualSelectTestsTreeComponentService {

    isEditMode: boolean = false;
/*
    isEditMode: boolean = false;

    jsonTreeModel: JsonTreeModel;

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

        let jsonTreeModel = ManualSelectTestsTreeUtil.createRootPackage();
        ManualSelectTestsTreeUtil.mapTestModelToTestJsonTreeModel(jsonTreeModel, this.manualTestRunner.testsToExecute, true);
        ManualSelectTestsTreeUtil.mapTestModelToTestJsonTreeModel(jsonTreeModel, testModels, false);
        this.jsonTreeModel =  jsonTreeModel;

        this.sort();

        this.calculateSelectionOfDirectories(jsonTreeModel.children[0] as SelectTestsTreeContainerModel)
    }

    private calculateSelectionOfDirectories(rootNode: SelectTestsTreeContainerModel) {
        for (const child of rootNode.children) {
            if(child.isContainer()) {
                this.calculateSelectionOfDirectories(child as SelectTestsTreeContainerModel)
            }
            this.calculateSelectionForNode(child)
        }
    }

    private calculateSelectionForNode(node: SelectTestsTreeNodeModel) {
        let parentNodes: Array<SelectTestsTreeContainerModel> = this.getParentNodesOf(
            node
        ) as Array<SelectTestsTreeContainerModel>;
        for (const parentNode of parentNodes) {
            parentNode.calculateCheckState()
        }
    }

    sort(): void {
        this.sortChildren(this.jsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: SelectTestsTreeNodeModel, right: SelectTestsTreeNodeModel) => {
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
                this.sortChildren((it as SelectTestsTreeContainerModel).children)
            }
        })
    }

    getParentNodesOf(node: JsonTreePathNode): Array<JsonTreePathContainer> {
        return this.getParentNodesOfPath(
            this.jsonTreeModel.children as Array<JsonTreePathNode>,
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
        let treeLeafs: Array<SelectTestsTreeNodeModel> = [];
        this.getTreeLeafs(this.jsonTreeModel.children, treeLeafs);

        for (const treeLeaf of treeLeafs) {
            if (treeLeaf.isSelected) {
                result.push(treeLeaf.payload)
            }
        }
        return result;
    }

    private getTreeLeafs(treeNodes: Array<JsonTreeNode>, result:Array<SelectTestsTreeNodeModel>) {
        for (const treeNode of treeNodes) {
            if (treeNode.isContainer()) {
                this.getTreeLeafs(
                    (treeNode as JsonTreeContainer).getChildren(),
                    result
                )
            }
            result.push(treeNode as SelectTestsTreeNodeModel)
        }
        return result;
    }*/
}
