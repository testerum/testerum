import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestsRunner} from "../../runner/model/manual-tests-runner.model";
import {ManualTestsRunnerService} from "../../runner/service/manual-tests-runner.service";
import {ManualTestsTreeExecutorNodeModel} from "./model/manual-tests-tree-executor-node.model";
import {ManualTestsTreeExecutorContainerModel} from "./model/manual-tests-tree-executor-container.model";
import {ManualTestStatus} from "../../model/enums/manual-test-status.enum";
import {ManualTestExeModel} from "../../runner/model/manual-test-exe.model";
import ManualTestsTreeExecutorUtil from "./util/manual-tests-tree-executor.util";

@Injectable()
export class ManualTestsExecutorTreeService {

    isEditMode: boolean = false;

    testsJsonTreeModel: JsonTreeModel;
    testModels: Array<ManualTestExeModel> = [];

    manualTestRunner: ManualTestsRunner;

    constructor(private manualTestsRunnerService: ManualTestsRunnerService){
    }

    initializeTestsTreeFromServer(path: Path) {
        this.manualTestsRunnerService.getTestRunner(path.toString()).subscribe(
            (manualTestRunner:ManualTestsRunner) => {
                this.initializeTestsTree(manualTestRunner)
            }
        )
    }

    initializeTestsTree(manualTestRunner: ManualTestsRunner) {
        this.manualTestRunner = manualTestRunner;


        let jsonTreeModel = ManualTestsTreeExecutorUtil.createRootPackage();
        ManualTestsTreeExecutorUtil.mapTestModelToTestJsonTreeModel(jsonTreeModel, this.manualTestRunner.testsToExecute, true);
        this.testsJsonTreeModel =  jsonTreeModel;

        this.sort();

        this.testModels.length = 0;
        this.initTestModelsFromTreeToPreserveOrder(jsonTreeModel.children[0] as ManualTestsTreeExecutorContainerModel);

        this.calculateTreeNodesState(jsonTreeModel.children[0] as ManualTestsTreeExecutorContainerModel)
    }

    private initTestModelsFromTreeToPreserveOrder(rootNode: ManualTestsTreeExecutorContainerModel) {
        for (const child of rootNode.children) {
            if (child.isContainer()) {
                this.initTestModelsFromTreeToPreserveOrder(child as ManualTestsTreeExecutorContainerModel)
            } else {
                this.testModels.push(child.payload)
            }
        }
    }

    sort(): void {
        this.sortChildren(this.testsJsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: ManualTestsTreeExecutorNodeModel, right: ManualTestsTreeExecutorNodeModel) => {
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
                this.sortChildren((it as ManualTestsTreeExecutorContainerModel).children)
            }
        })
    }

    private calculateTreeNodesState(rootNode: ManualTestsTreeExecutorContainerModel) {

        rootNode.testsState = ManualTestStatus.NOT_APPLICABLE;
        for (const childNode of rootNode.children) {
            if(childNode.isContainer()) {
                let containerChildNode = childNode as ManualTestsTreeExecutorContainerModel;
                this.calculateTreeNodesState(containerChildNode);

                rootNode.totalTests += containerChildNode.totalTests;
                rootNode.notExecutedTests += containerChildNode.notExecutedTests;
                rootNode.passedTests += containerChildNode.passedTests;
                rootNode.failedTests += containerChildNode.failedTests;
                rootNode.blockedTests += containerChildNode.blockedTests;
                rootNode.notApplicableTests += containerChildNode.notApplicableTests;

                rootNode.testsState = this.calculateRootNodeStateBasedOnNode(rootNode.testsState, containerChildNode.testsState)
            } else {
                rootNode.totalTests ++;

                switch(childNode.payload.testStatus) {
                    case ManualTestStatus.NOT_EXECUTED : {rootNode.notExecutedTests++; break;}
                    case ManualTestStatus.PASSED : {rootNode.passedTests++; break;}
                    case ManualTestStatus.FAILED : {rootNode.failedTests++; break;}
                    case ManualTestStatus.BLOCKED : {rootNode.blockedTests++; break;}
                    case ManualTestStatus.NOT_APPLICABLE : {rootNode.notApplicableTests++; break;}
                }

                rootNode.testsState = this.calculateRootNodeStateBasedOnNode(rootNode.testsState, childNode.payload.testStatus)
            }
        }
    }

    private calculateRootNodeStateBasedOnNode(rootNodeState: ManualTestStatus, childNodeState: ManualTestStatus): ManualTestStatus {
        if(rootNodeState == ManualTestStatus.NOT_EXECUTED || childNodeState == ManualTestStatus.NOT_EXECUTED) return ManualTestStatus.NOT_EXECUTED;
        if(rootNodeState == ManualTestStatus.FAILED || childNodeState == ManualTestStatus.FAILED) return ManualTestStatus.FAILED;
        if(rootNodeState == ManualTestStatus.PASSED || childNodeState == ManualTestStatus.PASSED) return ManualTestStatus.PASSED;
        if(rootNodeState == ManualTestStatus.BLOCKED || childNodeState == ManualTestStatus.BLOCKED) return ManualTestStatus.BLOCKED;

        return ManualTestStatus.NOT_APPLICABLE
    }

    getNextUnExecutedTest(currentManualTestPath: Path): ManualTestExeModel {
        let currentTestIndex: number = this.testModels.findIndex( (testModel: ManualTestExeModel) => {
            return testModel.path.equals(currentManualTestPath)
        });

        for (let i = currentTestIndex + 1 ; i < this.testModels.length; i++) {
            if (this.testModels[i].testStatus == ManualTestStatus.NOT_EXECUTED) {
                return this.testModels[i];
            }
        }

        for (const testModel of this.testModels) {
            if (testModel.testStatus == ManualTestStatus.NOT_EXECUTED) {
                return testModel;
            }
        }
        return null;
    }
}
