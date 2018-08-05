import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {RunnerResultDirInfo} from "../../model/report/runner-result-dir-info.model";
import {Injectable} from "@angular/core";
import {Path} from "../../model/infrastructure/path/path.model";
import {RunnerEvent, RunnerEventMarshaller} from "../../model/test/event/runner.event";
import {HttpClient, HttpParams} from "@angular/common/http";

@Injectable()
export class ResultService {

    private BASE_URL = "/rest/runResults";

    constructor(private http: HttpClient) {}

    getRunnerReportDirInfo(): Observable<Array<RunnerResultDirInfo>> {

        return this.http
            .get<Array<RunnerResultDirInfo>>(this.BASE_URL).pipe(
            map(ResultService.extractRunnerReportDirInfo));
    }

    private static extractRunnerReportDirInfo(res: Array<RunnerResultDirInfo>): Array<RunnerResultDirInfo> {

        let response: Array<RunnerResultDirInfo> = [];
        for (let reportAsJson of res) {
            let report = new RunnerResultDirInfo().deserialize(reportAsJson);
            response.push(report)
        }

        return response;
    }

    getResult(path: Path): Observable<Array<RunnerEvent>> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<Array<RunnerEvent>>(this.BASE_URL, httpOptions)
            .pipe(map(ResultService.extractRunnerResult));
    }

    private static extractRunnerResult(res: Array<RunnerEvent>):Array<RunnerEvent> {
        let result:Array<RunnerEvent> = [];
        for (let event of res) {
            result.push(
                RunnerEventMarshaller.deserializeRunnerEvent(event)
            )
        }
        return result;
    }
}
