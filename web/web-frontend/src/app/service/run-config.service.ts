import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {RunConfig} from "../functionalities/config/run-config/model/runner-config.model";
import {map} from "rxjs/operators";
import {JsonUtil} from "../utils/json.util";

@Injectable()
export class RunConfigService {

    private BASE_URL = "/rest/run-configs";

    constructor(private http: HttpClient) {}

    getRunnerConfig(): Observable<RunConfig[]> {
        return this.http
            .get<RunConfig[]>(this.BASE_URL)
            .pipe(map(RunConfigService.extractSettings));
    }

    private static extractSettings(res:  RunConfig[]): RunConfig[] {
        const response: RunConfig[] = [];

        for (let runnerConfigAsJson of res) {
            let runnerConfig = new RunConfig().deserialize(runnerConfigAsJson);
            response.push(runnerConfig)
        }

        return response;
    }

    saveRunnerConfig(runnerConfigs: RunConfig[]) {
        const body = JsonUtil.serializeArrayOfSerializable(runnerConfigs);
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL, body, httpOptions).pipe(
                map(RunConfigService.extractSettings));
    }
}
