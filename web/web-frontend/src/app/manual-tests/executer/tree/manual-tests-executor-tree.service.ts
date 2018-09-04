import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestsRunner} from "../../runner/model/manual-tests-runner.model";
import {ManualTestsRunnerService} from "../../runner/service/manual-tests-runner.service";
import {ManualTestsTreeExecutorNodeModel} from "./model/manual-tests-tree-executor-node.model";
import {ManualTestsTreeExecutorContainerModel} from "./model/manual-tests-tree-executor-container.model";
import {OldManualTestStatus} from "../../model/enums/manual-test-status.enum";
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

        rootNode.testsState = OldManualTestStatus.NOT_APPLICABLE;
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
                    case OldManualTestStatus.NOT_EXECUTED : {rootNode.notExecutedTests++; break;}
                    case OldManualTestStatus.PASSED : {rootNode.passedTests++; break;}
                    case OldManualTestStatus.FAILED : {rootNode.failedTests++; break;}
                    case OldManualTestStatus.BLOCKED : {rootNode.blockedTests++; break;}
                    case OldManualTestStatus.NOT_APPLICABLE : {rootNode.notApplicableTests++; break;}
                }

                rootNode.testsState = this.calculateRootNodeStateBasedOnNode(rootNode.testsState, childNode.payload.testStatus)
            }
        }
    }

    private calculateRootNodeStateBasedOnNode(rootNodeState: OldManualTestStatus, childNodeState: OldManualTestStatus): OldManualTestStatus {
        if(rootNodeState == OldManualTestStatus.NOT_EXECUTED || childNodeState == OldManualTestStatus.NOT_EXECUTED) return OldManualTestStatus.NOT_EXECUTED;
        if(rootNodeState == OldManualTestStatus.FAILED || childNodeState == OldManualTestStatus.FAILED) return OldManualTestStatus.FAILED;
        if(rootNodeState == OldManualTestStatus.PASSED || childNodeState == OldManualTestStatus.PASSED) return OldManualTestStatus.PASSED;
        if(rootNodeState == OldManualTestStatus.BLOCKED || childNodeState == OldManualTestStatus.BLOCKED) return OldManualTestStatus.BLOCKED;

        return OldManualTestStatus.NOT_APPLICABLE
    }

    getNextUnExecutedTest(currentManualTestPath: Path): ManualTestExeModel {
        let currentTestIndex: number = this.testModels.findIndex( (testModel: ManualTestExeModel) => {
            return testModel.path.equals(currentManualTestPath)
        });

        for (let i = currentTestIndex + 1 ; i < this.testModels.length; i++) {
            if (this.testModels[i].testStatus == OldManualTestStatus.NOT_EXECUTED) {
                return this.testModels[i];
            }
        }

        for (const testModel of this.testModels) {
            if (testModel.testStatus == OldManualTestStatus.NOT_EXECUTED) {
                return testModel;
            }
        }
        return null;
    }
}
