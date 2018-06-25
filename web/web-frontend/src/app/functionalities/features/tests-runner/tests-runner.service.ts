import {Injectable} from '@angular/core';
import {TestModel} from "../../../model/test/test.model";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";
import {TestWebSocketService} from "../../../service/test-web-socket.service";
import {RunnerTreeService} from "./tests-runner-tree/runner-tree.service";
import {TestsRunnerLogsService} from "./tests-runner-logs/tests-runner-logs.service";

@Injectable()
export class TestsRunnerService {

    private testModels: Array<TestModel>;
    private testModelSubject: Subject<Array<TestModel>> = new Subject<Array<TestModel>>();

    constructor(private testWebSocketService: TestWebSocketService,
                private runnerTreeService: RunnerTreeService,
                private testsRunnerLogsService: TestsRunnerLogsService) {}

    runTests(testModels: Array<TestModel>) {
        this.testModels = testModels;
        this.testModelSubject.next(testModels);
        this.runnerTreeService.initialize(testModels);
        this.testsRunnerLogsService.initialize();

        this.testWebSocketService.runTests(this.testModels);
    }

    getTestModel(): Observable<Array<TestModel>> {
        return this.testModelSubject.asObservable();
    }

}
