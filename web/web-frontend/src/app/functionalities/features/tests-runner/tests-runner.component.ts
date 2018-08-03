import {Component} from '@angular/core';
import {TestsRunnerService} from "./tests-runner.service";
import {TestsRunnerLogsService} from "./tests-runner-logs/tests-runner-logs.service";
import {RunnerTreeService} from "./tests-runner-tree/runner-tree.service";

@Component({
    moduleId: module.id,
    selector: 'tests-runner',
    templateUrl: 'tests-runner.component.html'
})

export class TestsRunnerComponent {

    public runnerTreeService: RunnerTreeService;
    public testsRunnerLogsService:TestsRunnerLogsService;

    constructor(private testsRunnerService: TestsRunnerService,
                runnerTreeService: RunnerTreeService,
                testsRunnerLogsService: TestsRunnerLogsService) {

        this.runnerTreeService = runnerTreeService;
        this.testsRunnerLogsService = testsRunnerLogsService;
    }

    isTestRunnerVisible(): boolean {
        return this.testsRunnerService.isTestRunnerVisible;
    }

    close() {
        this.testsRunnerService.isTestRunnerVisible = false;
    }
}
