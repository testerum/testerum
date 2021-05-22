import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {RunnerResultDirInfo} from "../../model/report/runner-result-dir-info.model";
import {Location} from "@angular/common";

@Injectable()
export class ResultService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/report-results");
    }

    getRunnerReportDirInfo(): Observable<Array<RunnerResultDirInfo>> {
        return this.http
            .get<Array<RunnerResultDirInfo>>(this.baseUrl).pipe(
                map(ResultService.extractRunnerReportDirInfo));
    }

    getStatisticsUrl(): Observable<string> {
        return this.http
            .get<string>(this.baseUrl + "/statistics-url").pipe(
                map((result) => result as string));
    }

    private static extractRunnerReportDirInfo(res: Array<RunnerResultDirInfo>): Array<RunnerResultDirInfo> {
        let response: Array<RunnerResultDirInfo> = [];
        for (let reportAsJson of res) {
            let report = new RunnerResultDirInfo().deserialize(reportAsJson);
            response.push(report)
        }

        return response;
    }

}
