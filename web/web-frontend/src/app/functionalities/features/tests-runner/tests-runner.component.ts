import {Component, OnInit} from '@angular/core';
import {TestsRunnerService} from "./tests-runner.service";
import {TestModel} from "../../../model/test/test.model";
import {TestsRunnerLogsService} from "./tests-runner-logs/tests-runner-logs.service";
import {RunnerTreeService} from "./tests-runner-tree/runner-tree.service";
import {ExecutionPieModel} from "../../../generic/components/charts/execution-pie/model/execution-pie.model";

@Component({
    moduleId: module.id,
    selector: 'tests-runner',
    templateUrl: 'tests-runner.component.html'
})

export class TestsRunnerComponent {

    public visible:boolean = false;
    public pieModel: ExecutionPieModel = new ExecutionPieModel();

    public runnerTreeService: RunnerTreeService;
    public testsRunnerLogsService:TestsRunnerLogsService;

    constructor(testsRunnerService: TestsRunnerService,
                runnerTreeService: RunnerTreeService,
                testsRunnerLogsService: TestsRunnerLogsService) {

        testsRunnerService.getTestModel().subscribe(
            testModels => this.setTestModel(testModels)
        );

        this.runnerTreeService = runnerTreeService;
        this.testsRunnerLogsService = testsRunnerLogsService;
    }

    private setTestModel(testModels:Array<TestModel>) {
        this.visible = true;
    }

    close() {
        this.visible = false;
    }
}
