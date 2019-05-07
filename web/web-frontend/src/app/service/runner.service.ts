import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RunnerConfig} from "../functionalities/config/runner/model/runner-config.model";
import {map} from "rxjs/operators";

@Injectable()
export class RunnerService {

    private BASE_URL = "/rest/runner";

    constructor(private http: HttpClient) {}

    getRunnerConfig(): Observable<RunnerConfig[]> {
        return this.http
            .get<RunnerConfig[]>(this.BASE_URL)
            .pipe(map(RunnerService.extractSettings));
    }

    private static extractSettings(res:  RunnerConfig[]): RunnerConfig[] {
        const response: RunnerConfig[] = [];

        for (let runnerConfigAsJson of res) {
            let runnerConfig = new RunnerConfig().deserialize(runnerConfigAsJson);
            response.push(runnerConfig)
        }

        return response;
    }
}
