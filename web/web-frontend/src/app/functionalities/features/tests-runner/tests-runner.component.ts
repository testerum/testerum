import {Component} from '@angular/core';
import {TestsRunnerService} from "./tests-runner.service";
import {TestsRunnerLogsService} from "./tests-runner-logs/tests-runner-logs.service";
import {RunnerTreeComponentService} from "./tests-runner-tree/runner-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'tests-runner',
    templateUrl: 'tests-runner.component.html'
})

export class TestsRunnerComponent {

    constructor(private testsRunnerService: TestsRunnerService,
                public runnerTreeService: RunnerTreeComponentService,
                public testsRunnerLogsService: TestsRunnerLogsService) { }

    isTestRunnerVisible(): boolean {
        return this.testsRunnerService.isTestRunnerVisible;
    }

    close() {
        this.testsRunnerService.isTestRunnerVisible = false;
        this.testsRunnerService.stopExecution();
    }
}
