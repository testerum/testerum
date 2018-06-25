import {Http, RequestOptions, Response, Headers} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import {Router} from "@angular/router";
import {Subject} from "rxjs/Rx";
import {ErrorService} from "../error.service";
import {RunnerResultDirInfo} from "../../model/report/runner-result-dir-info.model";
import {Injectable} from "@angular/core";
import {Path} from "../../model/infrastructure/path/path.model";
import {RunnerEvent} from "../../model/test/event/runner.event";
import {TestWebSocketService} from "../test-web-socket.service";

@Injectable()
export class ResultService {

    private BASE_URL = "/rest/runResults";

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    getRunnerReportDirInfo(): Observable<Array<RunnerResultDirInfo>> {

        return this.http
            .get(this.BASE_URL)
            .map(ResultService.extractRunnerReportDirInfo)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractRunnerReportDirInfo(res: Response): Array<RunnerResultDirInfo> {
        let json = res.json();

        let response: Array<RunnerResultDirInfo> = [];
        for (let reportAsJson of json) {
            let report = new RunnerResultDirInfo().deserialize(reportAsJson);
            response.push(report)
        }

        return response;
    }

    getResult(path: Path): Observable<Array<RunnerEvent>> {
        return this.http
            .get(this.BASE_URL, {params: {path: path.toString()}})
            .map(ResultService.extractRunnerResult)
            .catch(err => {return this.errorService.handleHttpResponseException(err)} );
    }

    private static extractRunnerResult(res: Response):Array<RunnerEvent> {
        let json = res.json();
        let result:Array<RunnerEvent> = [];
        for (let event of json) {
            result.push(
                TestWebSocketService.deserializeRunnerEvent(event)
            )
        }
        return result;
    }

}
