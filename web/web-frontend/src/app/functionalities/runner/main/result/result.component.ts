import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {TestsRunnerLogsService} from "../../../features/tests-runner/tests-runner-logs/tests-runner-logs.service";
import {TestsRunnerLogModel} from "../../../features/tests-runner/tests-runner-logs/model/tests-runner-log.model";
import {RunnerTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-tree-node.model";
import {TestsRunnerLogsComponent} from "../../../features/tests-runner/tests-runner-logs/tests-runner-logs.component";
import {ExecutionPieService} from "../../../../generic/components/charts/execution-pie/execution-pie.service";
import {RunnerEventTypeEnum} from "../../../../model/test/event/enums/runner-event-type.enum";
import {SuiteStartEvent} from "../../../../model/test/event/suite-start.event";
import {RunnerRootTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-root-tree-node.model";
import {SuiteEndEvent} from "../../../../model/test/event/suite-end.event";
import {TestStartEvent} from "../../../../model/test/event/test-start.event";
import {RunnerTreeNodeTypeEnum} from "../../../features/tests-runner/tests-runner-tree/model/enums/runner-tree-node-type.enum";
import {TestEndEvent} from "../../../../model/test/event/test-end.event";
import {StepStartEvent} from "../../../../model/test/event/step-start.event";
import {StepEndEvent} from "../../../../model/test/event/step-end.event";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerTestTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-test-tree-node.model";
import {RunnerTreeContainerNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-tree-container-node.model";
import {ExecutionStatusEnum} from "../../../../model/test/event/enums/execution-status.enum";
import {FeatureStartEvent} from "../../../../model/test/event/feature-start.event";
import {FeatureEndEvent} from "../../../../model/test/event/feature-end.event";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {RunnerComposedStepTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-basic-step-tree-node.model";
import {RunnerFeatureTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-feature-tree-node.model";

@Component({
    selector: 'result',
    templateUrl: 'result.component.html'
})

export class ResultComponent implements OnInit {

    treeModel: JsonTreeModel = new JsonTreeModel();
    logs: Array<TestsRunnerLogModel> = [];
    runnerEvents: Array<RunnerEvent> = [];

    @ViewChild(TestsRunnerLogsComponent) testsRunnerLogsComponent: TestsRunnerLogsComponent;

    constructor(private route: ActivatedRoute,
                private executionPieService: ExecutionPieService) {
    }

    ngOnInit() {
        this.route.data.subscribe(data => {
            let eventsFromServer: Array<RunnerEvent> = data['runnerEvents'];

            this.initializeResultTree(eventsFromServer);
            this.initializeLogs(eventsFromServer);
        });
    }

    private initializeLogs(eventsFromServer: Array<RunnerEvent>) {
        this.logs.length = 0;
        for (let event of eventsFromServer) {
            this.runnerEvents.push(event);

            this.logs.push(
                TestsRunnerLogsService.transformEvent(event)
            )
        }
        this.testsRunnerLogsComponent.updateLogsToDisplayFromLogs();
    }

    private initializeResultTree(eventsFromServer: Array<RunnerEvent>) {
        this.treeModel.children.length = 0;
        this.executionPieService.pieModel.reset();

        let parentNodesStack: Array<RunnerTreeNodeModel> = [];

        for (const event of eventsFromServer) {

            if (event.eventType == RunnerEventTypeEnum.TEST_SUITE_START_EVENT) {
                let suiteStartEvent = event as SuiteStartEvent;

                let currentNode = new RunnerRootTreeNodeModel(this.treeModel);
                this.treeModel.children.push(currentNode);

                currentNode.id = "TestSuite";
                currentNode.eventKey = suiteStartEvent.eventKey;
                currentNode.state = ExecutionStatusEnum.FAILED;
                currentNode.getNodeState().showChildren = true;

                this.pushNodeToStack(parentNodesStack, currentNode);
            }

            if (event.eventType == RunnerEventTypeEnum.TEST_SUITE_END_EVENT) {
                let currentNode = this.popNodeFromStack(parentNodesStack);
                currentNode.state = (event as SuiteEndEvent).status;
            }

            if (event.eventType == RunnerEventTypeEnum.FEATURE_START_EVENT) {
                let featureStartEvent = event as FeatureStartEvent;

                let parentNode = this.getLastNodeFromStack(parentNodesStack) as RunnerTreeContainerNodeModel;
                let currentNode = new RunnerFeatureTreeNodeModel(parentNode);
                parentNode.getChildren().push(currentNode);

                currentNode.id = featureStartEvent.eventKey.getCurrentNodeId();
                currentNode.eventKey = featureStartEvent.eventKey;
                currentNode.text = featureStartEvent.featureName;
                currentNode.state = ExecutionStatusEnum.FAILED;
                currentNode.getNodeState().showChildren = true;

                this.pushNodeToStack(parentNodesStack, currentNode);
            }

            if (event.eventType == RunnerEventTypeEnum.FEATURE_END_EVENT) {
                let currentNode = this.popNodeFromStack(parentNodesStack);
                let testEndEvent = event as FeatureEndEvent;
                currentNode.state = testEndEvent.status;
            }

            if (event.eventType == RunnerEventTypeEnum.TEST_START_EVENT) {
                this.executionPieService.pieModel.totalTests++;

                let testStartEvent = event as TestStartEvent;

                let parentNode = this.getLastNodeFromStack(parentNodesStack) as RunnerTreeContainerNodeModel;
                let currentNode = new RunnerTestTreeNodeModel(parentNode);
                parentNode.getChildren().push(currentNode);

                currentNode.id = testStartEvent.eventKey.getCurrentNodeId();
                currentNode.path = testStartEvent.testFilePath;
                currentNode.eventKey = testStartEvent.eventKey;
                currentNode.text = testStartEvent.testName;
                currentNode.state = ExecutionStatusEnum.FAILED;
                currentNode.getNodeState().showChildren = false;

                this.pushNodeToStack(parentNodesStack, currentNode);
            }

            if (event.eventType == RunnerEventTypeEnum.TEST_END_EVENT) {
                let currentNode = this.popNodeFromStack(parentNodesStack);
                let testEndEvent = event as TestEndEvent;
                currentNode.state = testEndEvent.status;
                this.executionPieService.pieModel.incrementBasedOnState(testEndEvent.status)
            }

            if (event.eventType == RunnerEventTypeEnum.STEP_START_EVENT) {
                let stepStartEvent = event as StepStartEvent;

                let parentNode = this.getLastNodeFromStack(parentNodesStack) as RunnerTreeContainerNodeModel;
                let currentNode;
                let stepDef = stepStartEvent.stepCall.stepDef;
                if (stepDef instanceof ComposedStepDef) {
                    currentNode = new RunnerComposedStepTreeNodeModel(parentNode);
                    currentNode.id = stepStartEvent.eventKey.getCurrentNodeId();
                    // currentNode.path = stepStartEvent.path //TODO: StepStartEvent should have the step path too
                    currentNode.eventKey = stepStartEvent.eventKey;
                    currentNode.getNodeState().showChildren = false;
                    currentNode.stepCall = stepStartEvent.stepCall;
                } else {
                    currentNode = new RunnerBasicStepTreeNodeModel(parentNode);
                    currentNode.id = stepStartEvent.eventKey.getCurrentNodeId();
                    // currentNode.path = stepStartEvent.path //TODO: StepStartEvent should have the step path too
                    currentNode.eventKey = stepStartEvent.eventKey;
                    currentNode.stepCall = stepStartEvent.stepCall;
                }
                currentNode.state = ExecutionStatusEnum.FAILED;

                parentNode.getChildren().push(currentNode);

                this.pushNodeToStack(parentNodesStack, currentNode);
            }

            if (event.eventType == RunnerEventTypeEnum.STEP_END_EVENT) {
                let currentNode = this.popNodeFromStack(parentNodesStack);
                let stepEndEvent = event as StepEndEvent;
                currentNode.state = stepEndEvent.status;
            }
        }
    }

    private pushNodeToStack(parentNodesStack: Array<RunnerTreeNodeModel>, currentNode:RunnerTreeNodeModel) {
        parentNodesStack.push(currentNode);
    }

    private popNodeFromStack(parentNodesStack: Array<RunnerTreeNodeModel>): RunnerTreeNodeModel {
        return parentNodesStack.pop();
    }

    private getLastNodeFromStack(parentNodesStack: Array<RunnerTreeNodeModel>): RunnerTreeNodeModel {
        return parentNodesStack[parentNodesStack.length - 1];
    }
}
