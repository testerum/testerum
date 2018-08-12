import {EventEmitter, Injectable} from "@angular/core";
import {RunnerTreeNodeModel} from "./model/runner-tree-node.model";
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {ExecutionStatusEnum} from "../../../../model/test/event/enums/execution-status.enum";
import {ExecutionPieService} from "../../../../generic/components/charts/execution-pie/execution-pie.service";
import {ExecutionPieModel} from "../../../../generic/components/charts/execution-pie/model/execution-pie.model";
import {TestsRunnerService} from "../tests-runner.service";
import {Subscription} from "rxjs";
import {RunnerRootNode} from "../../../../model/runner/tree/runner-root-node.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerTreeUtil} from "./util/runner-tree.util";
import {RunnerTestTreeNodeModel} from "./model/runner-test-tree-node.model";
import {RunnerRootTreeNodeModel} from "./model/runner-root-tree-node.model";
import {SuiteStartEvent} from "../../../../model/test/event/suite-start.event";
import {SuiteEndEvent} from "../../../../model/test/event/suite-end.event";
import {TestStartEvent} from "../../../../model/test/event/test-start.event";
import {TestEndEvent} from "../../../../model/test/event/test-end.event";
import {StepStartEvent} from "../../../../model/test/event/step-start.event";
import {StepEndEvent} from "../../../../model/test/event/step-end.event";
import {RunnerErrorEvent} from "../../../../model/test/event/runner-error.event";
import {RunnerTreeContainerNodeModel} from "./model/runner-tree-container-node.model";
import {EventKey} from "../../../../model/test/event/fields/event-key.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {PositionInParent} from "../../../../model/test/event/fields/position-in-parent.model";
import {FeatureStartEvent} from "../../../../model/test/event/feature-start.event";
import {FeatureEndEvent} from "../../../../model/test/event/feature-end.event";

@Injectable()
export class RunnerTreeComponentService {

    treeModel: JsonTreeModel;
    treeRootNode: RunnerRootTreeNodeModel;
    treeTestsNodes: RunnerTestTreeNodeModel[] = [];
    treeTestsWithFoldersNodes: RunnerTestTreeNodeModel[] = [];

    selectedRunnerTreeNode: RunnerTreeNodeModel;
    selectedRunnerTreeNodeObserver: EventEmitter<RunnerTreeNodeModel> = new EventEmitter<RunnerTreeNodeModel>();

    private runnerEventSubscription: Subscription = null;

    constructor(private testsRunnerService: TestsRunnerService,
                private executionPieService: ExecutionPieService) {
    }

    private onRunnerEvent(runnerEvent: RunnerEvent): void {
        if(runnerEvent instanceof SuiteStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.treeRootNode;
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof SuiteEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.treeRootNode;
            runnerTreeNode.changeState(runnerEvent.status)
        }
        if(runnerEvent instanceof FeatureStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof FeatureEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
        }
        if(runnerEvent instanceof TestStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof TestEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.changeState(runnerEvent.status);

            switch (runnerEvent.status) {
                case ExecutionStatusEnum.PASSED: this.executionPieService.pieModel.incrementPassed(); break;
                case ExecutionStatusEnum.FAILED: this.executionPieService.pieModel.incrementFailed(); break;
                case ExecutionStatusEnum.ERROR: this.executionPieService.pieModel.incrementError(); break;
                case ExecutionStatusEnum.UNDEFINED: this.executionPieService.pieModel.incrementUndefined(); break;
                case ExecutionStatusEnum.SKIPPED: this.executionPieService.pieModel.incrementSkipped(); break;
            }
        }
        if(runnerEvent instanceof StepStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof StepEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.findNode(runnerEvent.eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
        }

        if(runnerEvent instanceof RunnerErrorEvent) {
            this.setStateOnAllUnexecutedNodes(this.treeRootNode, ExecutionStatusEnum.ERROR);
        }
    }

    private setStateOnAllUnexecutedNodes(treeNode: RunnerTreeNodeModel, state: ExecutionStatusEnum) {
        if (ExecutionStatusEnum.WAITING == treeNode.state || ExecutionStatusEnum.EXECUTING == treeNode.state) {
            treeNode.state = state;
            this.setStateOnParents(treeNode, state);
        }
        if (treeNode.isContainer()) {
            let treeNodeAsContainer = treeNode as RunnerTreeContainerNodeModel;
            for (let child of treeNodeAsContainer.getChildren()) {
                this.setStateOnAllUnexecutedNodes(child, state)
            }
        }
    }

    private setStateOnParents(childNode: RunnerTreeNodeModel, state: ExecutionStatusEnum) {
        let parentNode = childNode.getParent() as RunnerTreeContainerNodeModel;
        if (parentNode == null || parentNode.state == state) {
            return;
        }

        parentNode.state = state;
        this.setStateOnParents(parentNode, state);
    }

    onStartTestExecution(runnerRootNode: RunnerRootNode): void {
        RunnerTreeUtil.mapServerModelToTreeModel(runnerRootNode, this.treeModel);
        this.treeRootNode = this.treeModel.children[0] as RunnerRootTreeNodeModel;
        this.treeTestsWithFoldersNodes = ArrayUtil.copyArrayOfObjects(this.treeRootNode.children);
        this.treeTestsNodes = RunnerTreeUtil.getTreeTestNodes(this.treeModel.children[0] as RunnerRootTreeNodeModel);

        if (this.runnerEventSubscription) {
            this.runnerEventSubscription.unsubscribe();
        }
        this.runnerEventSubscription = this.testsRunnerService.runnerEventObservable.subscribe((runnerEvent) => {
            this.onRunnerEvent(runnerEvent);
        });

        this.restPieData(this.executionPieService.pieModel);
    }

    findNode(eventKey:EventKey): RunnerTreeNodeModel {
        let runnerTreeNodeModel = this.findNodeByPositionInParent(ArrayUtil.copyArray(eventKey.positionsFromRoot), (this.treeModel.children as RunnerTreeNodeModel[]));
        if (runnerTreeNodeModel == null) {
            console.warn("Couldn't find a coresponding tree node for event with key", eventKey, this.treeRootNode)
        }
        return runnerTreeNodeModel;
    }

    private findNodeByPositionInParent(remainingPositions: Array<PositionInParent>, nodes: RunnerTreeNodeModel[]): RunnerTreeNodeModel {
        let currentPosition = remainingPositions[0];
        remainingPositions.splice(0, 1);

        if(nodes.length < currentPosition.indexInParent) {
            return null
        }

        let currentNode = nodes[currentPosition.indexInParent];
        if(currentNode.id == currentPosition.id) {
            if (remainingPositions.length == 0) {
                return currentNode
            }
            if(currentNode instanceof RunnerTreeContainerNodeModel) {
                return this.findNodeByPositionInParent(remainingPositions, currentNode.getChildren());
            }
        }
        return null;
    }

    private restPieData(pieModel: ExecutionPieModel) {

        pieModel.reset();
        pieModel.totalTests = this.treeTestsNodes.length;
        pieModel.waitingToExecute = this.treeTestsNodes.length;
    }

    setNodeAsSelected(runnerTreeNodeModel: RunnerTreeNodeModel) {
        this.selectedRunnerTreeNode = runnerTreeNodeModel;

        this.selectedRunnerTreeNodeObserver.emit(runnerTreeNodeModel);
        this.testsRunnerService.setSelectedNode(runnerTreeNodeModel);
    }

    showTestFolders(showTestFolders: boolean) {
        if (showTestFolders) {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsWithFoldersNodes);
        } else {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsNodes);
        }
    }
}
