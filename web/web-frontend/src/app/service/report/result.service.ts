import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {RunnerResultsInfoModel} from "../../model/report/runner-results-info.model";

@Injectable()
export class ResultService {

    private BASE_URL = "/rest/report-results";

    constructor(private http: HttpClient) {}

    getRunnerReportDirInfo(): Observable<RunnerResultsInfoModel> {
        return this.http
            .get<RunnerResultsInfoModel>(this.BASE_URL).pipe(
            map(ResultService.extractRunnerReportDirInfo));
    }

    private static extractRunnerReportDirInfo(input: Object): RunnerResultsInfoModel {
        return new RunnerResultsInfoModel().deserialize(input);
    }

}
