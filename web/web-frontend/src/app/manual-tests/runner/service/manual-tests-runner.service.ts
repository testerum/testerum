import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {UpdateManualTestExecutionModel} from "../model/operation/update-manual-test-execution.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Injectable()
export class ManualTestsRunnerService {

    private BASE_URL = "/rest/manualTestsRunner";

    constructor(private http: HttpClient) {}

    save(manualTestsRunner: ManualTestsRunner): Observable<ManualTestsRunner> {
        let body = manualTestsRunner.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ManualTestsRunner>(this.BASE_URL + "/save", body, httpOptions).pipe(
                map(res => new ManualTestsRunner().deserialize(res)));
    }

    getTests(): Observable<Array<ManualTestsRunner>> {
        return this.http
            .get<Array<ManualTestsRunner>>(this.BASE_URL).pipe(
            map(ManualTestsRunnerService.extractTestsRunners));
    }

    getTestRunner(pathAsString: string): Observable<ManualTestsRunner> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<ManualTestsRunner>(this.BASE_URL, httpOptions).pipe(
            map(res => new ManualTestsRunner().deserialize(res)));
    }

    delete(manualTestRunner:ManualTestsRunner): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', manualTestRunner.path.toString())
        };

        return this.http
            .delete<void>(this.BASE_URL, httpOptions);
    }

    finalize(manualTestRunner:ManualTestsRunner): Observable<ManualTestsRunner> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', manualTestRunner.path.toString())
        };

        return this.http
            .post<ManualTestsRunner>(this.BASE_URL + "/finalize", null, httpOptions).pipe(
            map(res => new ManualTestsRunner().deserialize(res)));
    }

    bringBackInExecution(manualTestRunner:ManualTestsRunner): Observable<ManualTestsRunner> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', manualTestRunner.path.toString())
        };

        return this.http
            .post<ManualTestsRunner>(this.BASE_URL + "/bringBackInExecution", null, httpOptions).pipe(
            map(res => new ManualTestsRunner().deserialize(res)));
    }


    updateExecutedTest(updateManualTestExecution: UpdateManualTestExecutionModel): Observable<ManualTestsRunner> {
        let body = updateManualTestExecution.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ManualTestsRunner>(this.BASE_URL + "/updateTestExecution", body, httpOptions).pipe(
            map(res => new ManualTestsRunner().deserialize(res)));
    }

    private static extractTestsRunners(res: Array<ManualTestsRunner>):Array<ManualTestsRunner> {
        let response:Array<ManualTestsRunner> = [];
        for(let testsModelAsJson of res) {
            let testsModel = new ManualTestsRunner().deserialize(testsModelAsJson);
            response.push(testsModel)
        }

        return response;
    }
}
