import {Injectable} from '@angular/core';
import {TestModel} from "../../../model/test/test.model";
import {TestWebSocketService} from "../../../service/test-web-socket.service";
import {RunnerTreeService} from "./tests-runner-tree/runner-tree.service";
import {TestsRunnerLogsService} from "./tests-runner-logs/tests-runner-logs.service";

@Injectable()
export class TestsRunnerService {

    public isTestRunnerVisible:boolean = false;

    constructor(private testWebSocketService: TestWebSocketService,
                private runnerTreeService: RunnerTreeService,
                private testsRunnerLogsService: TestsRunnerLogsService) {}

    runTests(testModels: Array<TestModel>) {
        this.isTestRunnerVisible = true;

        this.runnerTreeService.initialize(testModels);
        this.testsRunnerLogsService.initialize();

        this.testWebSocketService.runTests(testModels);
    }

}
