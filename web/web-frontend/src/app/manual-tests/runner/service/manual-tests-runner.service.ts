import {Headers, Http, RequestOptions, Response} from "@angular/http";
import {Router} from "@angular/router";
import {ErrorService} from "../../../service/error.service";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {UpdateManualTestRunner} from "../model/operation/update-manual-test.runner";
import {UpdateManualTestExecutionModel} from "../model/operation/update-manual-test-execution.model";


@Injectable()
export class ManualTestsRunnerService {

    private BASE_URL = "/rest/manualTestsRunner";

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    createTestRunner(manualTestRunner:ManualTestsRunner): Observable<ManualTestsRunner> {
        let body = manualTestRunner.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
            .post(this.BASE_URL + "/create", body, options)
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getTests(): Observable<Array<ManualTestsRunner>> {
        return this.http
            .get(this.BASE_URL)
            .map(ManualTestsRunnerService.extractTestsRunners)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getTestRunner(pathAsString: string): Observable<ManualTestsRunner> {
        return this.http
            .get(this.BASE_URL, {params: {path: pathAsString}})
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)} );
    }

    delete(manualTestRunner:ManualTestsRunner): Observable<void> {
        return this.http
            .delete(this.BASE_URL, {params: {path: manualTestRunner.path.toString()}})
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    finalize(manualTestRunner:ManualTestsRunner): Observable<ManualTestsRunner> {
        return this.http
            .post(this.BASE_URL + "/finalize", null,{params: {path: manualTestRunner.path.toString()}})
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    bringBackInExecution(manualTestRunner:ManualTestsRunner): Observable<ManualTestsRunner> {
        return this.http
            .post(this.BASE_URL + "/bringBackInExecution", null,{params: {path: manualTestRunner.path.toString()}})
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    updateTest(updateManualTestRunner:UpdateManualTestRunner): Observable<ManualTestsRunner> {
        let body = updateManualTestRunner.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
            .post(this.BASE_URL + "/update", body, options)
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }


    updateExecutedTest(updateManualTestExecution: UpdateManualTestExecutionModel): Observable<ManualTestsRunner> {
        let body = updateManualTestExecution.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
            .post(this.BASE_URL + "/updateTestExecution", body, options)
            .map(ManualTestsRunnerService.extractTestsRunner)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractTestsRunners(res: Response):Array<ManualTestsRunner> {
        let json = res.json();

        let response:Array<ManualTestsRunner> = [];
        for(let testsModelAsJson of json) {
            let testsModel = new ManualTestsRunner().deserialize(testsModelAsJson);
            response.push(testsModel)
        }

        return response;
    }

    private static extractTestsRunner(res: Response):ManualTestsRunner {
        let json = res.json();
        return new ManualTestsRunner().deserialize(json);
    }
}
