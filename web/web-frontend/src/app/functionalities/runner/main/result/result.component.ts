import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {TestsRunnerLogsService} from "../../../features/tests-runner/tests-runner-logs/tests-runner-logs.service";
import {TestsRunnerLogModel} from "../../../features/tests-runner/tests-runner-logs/model/tests-runner-log.model";
import {RunnerTreeComponentService} from "../../../features/tests-runner/tests-runner-tree/runner-tree.component-service";
import {ExecutionStatusEnum} from "../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeModel} from "../../../features/tests-runner/tests-runner-tree/model/runner-tree-node.model";
import {RunnerTreeNodeTypeEnum} from "../../../features/tests-runner/tests-runner-tree/model/enums/runner-tree-node-type.enum";
import {RunnerEventTypeEnum} from "../../../../model/test/event/enums/runner-event-type.enum";
import {SuiteEndEvent} from "../../../../model/test/event/suite-end.event";
import {TestStartEvent} from "../../../../model/test/event/test-start.event";
import {TestEndEvent} from "../../../../model/test/event/test-end.event";
import {StepStartEvent} from "../../../../model/test/event/step-start.event";
import {StepEndEvent} from "../../../../model/test/event/step-end.event";
import {SuiteStartEvent} from "../../../../model/test/event/suite-start.event";
import {TestsRunnerLogsComponent} from "../../../features/tests-runner/tests-runner-logs/tests-runner-logs.component";
import {ExecutionPieService} from "../../../../generic/components/charts/execution-pie/execution-pie.service";

@Component({
    selector: 'result',
    templateUrl: 'result.component.html'
})

export class ResultComponent implements OnInit {

    treeRootNode: RunnerTreeNodeModel;
    logs: Array<TestsRunnerLogModel> = [];
    runnerEvents: Array<RunnerEvent> = [];

    @ViewChild(TestsRunnerLogsComponent) testsRunnerLogsComponent: TestsRunnerLogsComponent;

    constructor(private route: ActivatedRoute,
                // private runnerTreeService: RunnerTreeComponentService,
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
        // this.executionPieService.pieModel.reset();
        //
        // let treeRootNode = this.runnerTreeService.createRootRunnerNode();
        //
        // this.populateTreeRootNode(eventsFromServer, treeRootNode);
        // this.treeRootNode = treeRootNode;
    }

    private populateTreeRootNode(eventsFromServer: Array<RunnerEvent>, rootNode: RunnerTreeNodeModel) {

        // rootNode.children.length = 0;
        // rootNode.state = ExecutionStatusEnum.FAILED;
        //
        // let currentTestNode: RunnerTreeNodeModel;
        // let steps: Array<RunnerTreeNodeModel> = [];
        //
        // for (const event of eventsFromServer) {
        //
        //     if (event.eventType == RunnerEventTypeEnum.TEST_SUITE_START_EVENT) {
        //         let suiteStartEvent = event as SuiteStartEvent;
        //         rootNode.id = suiteStartEvent.eventKey.getCurrentNodeId();
        //         rootNode.eventKey = suiteStartEvent.eventKey;
        //     }
        //
        //     if (event.eventType == RunnerEventTypeEnum.TEST_SUITE_END_EVENT) {
        //         rootNode.state = (event as SuiteEndEvent).status;
        //     }
        //
        //     if (event.eventType == RunnerEventTypeEnum.TEST_START_EVENT) {
        //         this.executionPieService.pieModel.totalTests++;
        //
        //         let currentTest = event as TestStartEvent;
        //
        //         currentTestNode = new RunnerTreeNodeModel();
        //         currentTestNode.id = currentTest.eventKey.getCurrentNodeId();
        //         currentTestNode.eventKey = currentTest.eventKey;
        //         currentTestNode.type = RunnerTreeNodeTypeEnum.TEST;
        //         currentTestNode.text = currentTest.testName;
        //
        //         currentTestNode.parent = rootNode;
        //         rootNode.children.push(currentTestNode);
        //     }
        //
        //     if (event.eventType == RunnerEventTypeEnum.TEST_END_EVENT) {
        //         let testEndEvent = event as TestEndEvent;
        //         currentTestNode.state = testEndEvent.status;
        //         this.executionPieService.pieModel.incrementBasedOnState(testEndEvent.status)
        //     }
        //
        //     if (event.eventType == RunnerEventTypeEnum.STEP_START_EVENT) {
        //         let stepStartEvent = event as StepStartEvent;
        //
        //         let runnerTreeNodeModel = new RunnerTreeNodeModel();
        //         runnerTreeNodeModel.id = stepStartEvent.eventKey.getCurrentNodeId();
        //         runnerTreeNodeModel.eventKey = stepStartEvent.eventKey;
        //         runnerTreeNodeModel.type = RunnerTreeNodeTypeEnum.STEP;
        //
        //         let previewStepPhase = steps.length == 0 ? null : steps[steps.length-1].stepCall.stepDef.phase;
        //         runnerTreeNodeModel.text = stepStartEvent.stepCall.getTextWithParamValues(previewStepPhase);
        //
        //         let parentTreeNode = steps.length == 0 ? currentTestNode : steps[steps.length - 1];
        //         runnerTreeNodeModel.parent = parentTreeNode;
        //         runnerTreeNodeModel.stepCall = stepStartEvent.stepCall;
        //
        //         parentTreeNode.children.push(runnerTreeNodeModel);
        //         steps.push(runnerTreeNodeModel)
        //     }
        //
        //     if (event.eventType == RunnerEventTypeEnum.STEP_END_EVENT) {
        //         let currentStepNode = steps.pop();
        //         if (currentStepNode) {
        //             currentStepNode.state = rootNode.state = (event as StepEndEvent).status;
        //         }
        //     }
        // }
    }
}
