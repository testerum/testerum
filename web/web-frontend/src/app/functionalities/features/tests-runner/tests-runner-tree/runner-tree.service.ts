import {Injectable} from "@angular/core";
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
import {ArrayUtil} from "../../../../utils/array.util";
import {FeatureStartEvent} from "../../../../model/test/event/feature-start.event";
import {FeatureEndEvent} from "../../../../model/test/event/feature-end.event";
import {RunnerTreeFilterModel} from "./model/filter/runner-tree-filter.model";
import {RunnerStoppedEvent} from '../../../../model/test/event/runner-stopped.event';
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ParametrizedTestStartEvent} from "../../../../model/test/event/parametrized-test-start.event";
import {ParametrizedTestEndEvent} from "../../../../model/test/event/parametrized-test-end.event";
import {ScenarioStartEvent} from "../../../../model/test/event/scenario-start.event";
import {ScenarioEndEvent} from "../../../../model/test/event/scenario-end.event";
import {JsonTreeExpandUtil} from "../../../../generic/components/json-tree/util/json-tree-expand.util";

@Injectable()
export class RunnerTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();
    hasFailedTests: boolean = false;

    private treeRootNode: RunnerRootTreeNodeModel;
    private treeTestsNodes: RunnerTestTreeNodeModel[] = [];
    private numberOfSimpleTestsAndScenarios: number = 0;
    private treeTestsWithFoldersNodes: RunnerTestTreeNodeModel[] = [];
    private allNodesMapByEventKey: Map<string, RunnerTreeNodeModel> = new Map<string, RunnerTreeNodeModel>();

    private currentTreeFilter: RunnerTreeFilterModel = new RunnerTreeFilterModel();

    private treeFilterSubscription: Subscription = null;
    private runnerEventSubscription: Subscription = null;
    private startTestExecutionSubscription: Subscription = null;

    constructor(private jsonTreeService: JsonTreeService,
                private testsRunnerService: TestsRunnerService,
                private executionPieService: ExecutionPieService) {

        this.onDestroy();
        if(!this.treeFilterSubscription) {
            this.treeFilterSubscription = testsRunnerService.treeFilterObservable.subscribe((filter: RunnerTreeFilterModel) => {
                this.currentTreeFilter = filter;
            });
        }

        if(!this.runnerEventSubscription) {
            this.runnerEventSubscription = this.testsRunnerService.runnerEventObservable.subscribe((runnerEvent) => {
                this.onRunnerEvent(runnerEvent);
            });
        }

        if(!this.startTestExecutionSubscription) {
            this.startTestExecutionSubscription = testsRunnerService.startTestExecutionObservable.subscribe((runnerRootNode: RunnerRootNode) => {
                this.onStartTestExecution(runnerRootNode);
            });
        }
    }

    onDestroy() {
        if(this.treeFilterSubscription) {
            this.treeFilterSubscription.unsubscribe();
        }

        if(this.runnerEventSubscription) {
            this.runnerEventSubscription.unsubscribe();
        }

        if(this.startTestExecutionSubscription) {
            this.startTestExecutionSubscription.unsubscribe();
        }
    }

    private onRunnerEvent(runnerEvent: RunnerEvent): void {
        let eventKey: string = runnerEvent.eventKey.eventKeyAsString;

        if (runnerEvent instanceof SuiteStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.treeRootNode;
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof SuiteEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.treeRootNode;
            runnerTreeNode.changeState(runnerEvent.status);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof FeatureStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof FeatureEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof TestStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof TestEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.changeState(runnerEvent.status);

            switch (runnerEvent.status) {
                case ExecutionStatusEnum.PASSED: this.executionPieService.pieModel.incrementPassed(); break;
                case ExecutionStatusEnum.FAILED: this.executionPieService.pieModel.incrementFailed(); this.hasFailedTests = true; break;
                case ExecutionStatusEnum.DISABLED: this.executionPieService.pieModel.incrementDisabled(); break;
                case ExecutionStatusEnum.UNDEFINED: this.executionPieService.pieModel.incrementUndefined(); break;
                case ExecutionStatusEnum.SKIPPED: this.executionPieService.pieModel.incrementSkipped(); break;
            }
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof ParametrizedTestStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof ParametrizedTestEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof ScenarioStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof ScenarioEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.changeState(runnerEvent.status);

            switch (runnerEvent.status) {
                case ExecutionStatusEnum.PASSED: this.executionPieService.pieModel.incrementPassed(); break;
                case ExecutionStatusEnum.FAILED: this.executionPieService.pieModel.incrementFailed(); this.hasFailedTests = true; break;
                case ExecutionStatusEnum.DISABLED: this.executionPieService.pieModel.incrementDisabled(); break;
                case ExecutionStatusEnum.UNDEFINED: this.executionPieService.pieModel.incrementUndefined(); break;
                case ExecutionStatusEnum.SKIPPED: this.executionPieService.pieModel.incrementSkipped(); break;
            }
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof StepStartEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof StepEndEvent) {
            let runnerTreeNode: RunnerTreeNodeModel = this.allNodesMapByEventKey.get(eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
            runnerTreeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);
            return;
        }

        if (runnerEvent instanceof RunnerErrorEvent) {
            this.setStateOnAllUnexecutedNodes(this.treeRootNode, ExecutionStatusEnum.FAILED);
            return;
        }

        if (runnerEvent instanceof RunnerStoppedEvent) {
            this.setStateOnAllUnexecutedNodes(this.treeRootNode, ExecutionStatusEnum.SKIPPED);
            return;
        }
    }

    private setStateOnAllUnexecutedNodes(treeNode: RunnerTreeNodeModel, state: ExecutionStatusEnum) {
        if (ExecutionStatusEnum.WAITING == treeNode.state || ExecutionStatusEnum.EXECUTING == treeNode.state) {
            treeNode.changeState(state);
            treeNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);

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
        if(childNode.getParent() instanceof JsonTreeModel) return;

        let parentNode = childNode.getParent() as RunnerTreeContainerNodeModel;
        if (parentNode == null || parentNode.state == state) {
            return;
        }

        //SKIPPED should overwrite only EXECUTING and WAITING state
        if (parentNode.state != ExecutionStatusEnum.EXECUTING &&
            parentNode.state != ExecutionStatusEnum.WAITING &&
            state == ExecutionStatusEnum.SKIPPED) {
            return;
        }

        parentNode.changeState(state);
        parentNode.calculateNodeVisibilityBasedOnFilter(this.currentTreeFilter);

        this.setStateOnParents(parentNode, state);
    }

    onStartTestExecution(runnerRootNode: RunnerRootNode): void {
        RunnerTreeUtil.mapServerModelToTreeModel(runnerRootNode, this.treeModel);
        this.treeRootNode = this.treeModel.children[0] as RunnerRootTreeNodeModel;
        this.treeTestsWithFoldersNodes = ArrayUtil.copyArrayOfObjects(this.treeRootNode.children);
        this.treeTestsNodes = RunnerTreeUtil.getTreeTestNodes(this.treeRootNode);
        this.numberOfSimpleTestsAndScenarios = RunnerTreeUtil.getNumberOfSimpleTestsAndScenarios(this.treeRootNode);
        this.allNodesMapByEventKey = RunnerTreeUtil.getAllNodesMapByEventKey(this.treeRootNode);

        if (this.treeTestsNodes.length == 1) {
            JsonTreeExpandUtil.expandTreeToLevel(this.treeModel,  100)
        }

        this.restPieData(this.executionPieService.pieModel);
        this.hasFailedTests = false;
    }

    private restPieData(pieModel: ExecutionPieModel) {
        pieModel.reset();
        pieModel.totalTests = this.numberOfSimpleTestsAndScenarios;
        pieModel.waitingToExecute = this.numberOfSimpleTestsAndScenarios;
    }

    setNodeAsSelected(runnerTreeNodeModel: RunnerTreeNodeModel) {
        this.testsRunnerService.setSelectedNode(runnerTreeNodeModel);

        this.jsonTreeService.setSelectedNode(runnerTreeNodeModel);
    }

    showTestFolders(showTestFolders: boolean) {
        if (showTestFolders) {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsWithFoldersNodes);
        } else {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsNodes);
        }
    }

    getFailedTestsPaths(): Path[] {
        let failedTestsPath: Path[] = [];

        this.allNodesMapByEventKey.forEach((value, key) => {
            if (value instanceof RunnerTestTreeNodeModel && value.state == ExecutionStatusEnum.FAILED) {
                failedTestsPath.push(value.path)
            }
        });

        return failedTestsPath;
    }
}
