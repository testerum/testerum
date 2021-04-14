import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {RunConfig} from "../functionalities/config/run-config/model/runner-config.model";
import {map} from "rxjs/operators";
import {JsonUtil} from "../utils/json.util";
import {Location} from "@angular/common";

@Injectable()
export class RunConfigService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/run-configs");
    }

    getRunnerConfig(): Observable<RunConfig[]> {
        return this.http
            .get<RunConfig[]>(this.baseUrl)
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
            .post<any>(this.baseUrl, body, httpOptions).pipe(
                map(RunConfigService.extractSettings));
    }
}
