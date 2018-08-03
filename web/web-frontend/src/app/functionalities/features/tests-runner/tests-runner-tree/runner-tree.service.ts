import {EventEmitter, Injectable} from "@angular/core";
import {BasicStepDef} from "../../../../model/basic-step-def.model";
import {TreeNodeModel} from "../../../../model/infrastructure/tree-node.model";
import {TestModel} from "../../../../model/test/test.model";
import {RunnerTreeNodeModel} from "./model/runner-tree-node.model";
import {RunnerTreeNodeTypeEnum} from "./model/enums/runner-tree-node-type.enum";
import {TestWebSocketService} from "../../../../service/test-web-socket.service";
import {WebSocketEventListener} from "../../../../service/event/web-socket-event.listener";
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {SuiteStartEvent} from "../../../../model/test/event/suite-start.event";
import {ExecutionStatusEnum} from "../../../../model/test/event/enums/execution-status.enum";
import {SuiteEndEvent} from "../../../../model/test/event/suite-end.event";
import {TestStartEvent} from "../../../../model/test/event/test-start.event";
import {TestEndEvent} from "../../../../model/test/event/test-end.event";
import {StepStartEvent} from "../../../../model/test/event/step-start.event";
import {StepEndEvent} from "../../../../model/test/event/step-end.event";
import {RunnerTreeNodeSelectedListener} from "./event/runner-tree-node-selected.listener";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {ComposedStepDef} from "../../../../model/composed-step-def.model";
import {StepCall} from "../../../../model/step-call.model";
import {RunnerErrorEvent} from "../../../../model/test/event/runner-error.event";
import {ExecutionPieService} from "../../../../generic/components/charts/execution-pie/execution-pie.service";
import {ExecutionPieModel} from "../../../../generic/components/charts/execution-pie/model/execution-pie.model";

@Injectable()
export class RunnerTreeService implements WebSocketEventListener {

    rootNode: RunnerTreeNodeModel = this.createRootRunnerNode();

    selectedStep: TreeNodeModel;
    selectedStepObserver: EventEmitter<TreeNodeModel> = new EventEmitter<TreeNodeModel>();

    private selectedRunnerTreeNode:RunnerTreeNodeModel = null;
    private selectedRunnerTreeNodeListeners: Array<RunnerTreeNodeSelectedListener> = [];

    constructor(testWebSocketService:TestWebSocketService,
                private executionPieService: ExecutionPieService) {
        testWebSocketService.addWebSocketEventListener(this);

        this.selectedStepObserver.subscribe((item: BasicStepDef) => this.selectedStep = item);
    }

    onWebSocketMessage(runnerEvent: RunnerEvent): void {

        if(runnerEvent instanceof SuiteStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode;
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof SuiteEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode;
            runnerTreeNode.changeState(runnerEvent.status)
        }
        if(runnerEvent instanceof TestStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode.findNode(runnerEvent.eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof TestEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode.findNode(runnerEvent.eventKey);
            runnerTreeNode.changeState(runnerEvent.status);

            switch (runnerEvent.status) {
                case ExecutionStatusEnum.PASSED: this.executionPieService.pieModel.incrementPassed(); break;
                case ExecutionStatusEnum.FAILED: this.executionPieService.pieModel.incrementFailed(); break;
                case ExecutionStatusEnum.ERROR: this.executionPieService.pieModel.incrementError(); break;
                case ExecutionStatusEnum.UNDEFINED: this.executionPieService.pieModel.incrementUndefined(); break;
                case ExecutionStatusEnum.SKIPPED: this.executionPieService.pieModel.incrementSkipped(); break;
            }

            this.executionPieService.pieModel.waitingToExecute --;
        }
        if(runnerEvent instanceof StepStartEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode.findNode(runnerEvent.eventKey);
            runnerTreeNode.eventKey = runnerEvent.eventKey;
            runnerTreeNode.changeState(ExecutionStatusEnum.EXECUTING)
        }
        if(runnerEvent instanceof StepEndEvent) {
            let runnerTreeNode:RunnerTreeNodeModel = this.rootNode.findNode(runnerEvent.eventKey);
            runnerTreeNode.changeState(runnerEvent.status);
        }

        if(runnerEvent instanceof RunnerErrorEvent) {
            this.setStateOnAllNodes(this.rootNode, ExecutionStatusEnum.ERROR);
        }
    }

    private setStateOnAllNodes(parentNode: RunnerTreeNodeModel, state: ExecutionStatusEnum) {
        parentNode.state = state;
        if (parentNode.children != null) {
            for (let child of parentNode.children) {
                this.setStateOnAllNodes(child, state)
            }
        }
    }

    initialize(testModels: Array<TestModel>): void {
        this.addTestToTreeModel(testModels, this.rootNode, this.executionPieService.pieModel);
    }

    private addTestToTreeModel(testModels: Array<TestModel>, rootNode: RunnerTreeNodeModel, pieModel: ExecutionPieModel) {
        pieModel.reset();
        pieModel.totalTests = testModels.length;
        pieModel.waitingToExecute = testModels.length;

        rootNode.children.length = 0;
        rootNode.state = ExecutionStatusEnum.WAITING;

        for (let testModel of testModels) {
            let runnerTreeNodeModel = new RunnerTreeNodeModel();
            runnerTreeNodeModel.id = testModel.id;
            runnerTreeNodeModel.type = RunnerTreeNodeTypeEnum.TEST;
            runnerTreeNodeModel.text = testModel.text;
            runnerTreeNodeModel.parent = this.rootNode;

            this.addStepsToTestRunnerNode(runnerTreeNodeModel, testModel.stepCalls);

            rootNode.children.push(runnerTreeNodeModel);
        }
    }

    private addStepsToTestRunnerNode(parent: RunnerTreeNodeModel, stepCalls: Array<StepCall>) {

        let previewsPhase:StepPhaseEnum = null;
        for (let stepCall of stepCalls) {
            let runnerTreeNodeModel = new RunnerTreeNodeModel();
            runnerTreeNodeModel.id = stepCall.id;
            runnerTreeNodeModel.type = RunnerTreeNodeTypeEnum.STEP;
            runnerTreeNodeModel.text = stepCall.getTextWithParamValues(previewsPhase);

            runnerTreeNodeModel.parent = parent;
            runnerTreeNodeModel.stepCall = stepCall;

            if(stepCall.stepDef instanceof ComposedStepDef) {
                this.addStepsToTestRunnerNode(runnerTreeNodeModel, stepCall.stepDef.stepCalls);
            }

            parent.children.push(runnerTreeNodeModel);

            previewsPhase = stepCall.stepDef.phase;
        }
    }

    createRootRunnerNode():RunnerTreeNodeModel {
        let runnerTreeNodeModel = new RunnerTreeNodeModel();
        runnerTreeNodeModel.id = "Features";
        runnerTreeNodeModel.type = RunnerTreeNodeTypeEnum.TESTS_ROOT;
        runnerTreeNodeModel.text = "Tests";

        return runnerTreeNodeModel;
    }

    addSelectedRunnerTreeNodeListeners(listener:RunnerTreeNodeSelectedListener) {
        this.selectedRunnerTreeNodeListeners.push(listener);
    }

    removeSelectedRunnerTreeNodeListeners(listener:RunnerTreeNodeSelectedListener) {
        let index: number = this.selectedRunnerTreeNodeListeners.indexOf(listener);
        if (index !== -1) {
            this.selectedRunnerTreeNodeListeners.splice(index, 1);
        }
    }

    setNodeAsSelected(runnerTreeNodeModel: RunnerTreeNodeModel) {
        this.selectedRunnerTreeNode = runnerTreeNodeModel;

        for (let listener of this.selectedRunnerTreeNodeListeners) {
            listener.onRunnerTreeNodeSelected(this.selectedRunnerTreeNode);
        }
    }
}
