import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Variable} from "../functionalities/variables/model/variable.model";
import {JsonUtil} from "../utils/json.util";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AllProjectVariables} from "../functionalities/variables/model/project-variables.model";

@Injectable()
export class VariablesService {

    private VARIABLES_URL = "/rest/variables";

    constructor(private http: HttpClient) {}

    getVariables(): Observable<AllProjectVariables> {
        return this.http
            .get<AllProjectVariables>(this.VARIABLES_URL).pipe(
            map(res => new AllProjectVariables().deserialize(res)));
    }

    save(model: AllProjectVariables): Observable<AllProjectVariables> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json'
            })
        };

        return this.http
            .post<AllProjectVariables>(this.VARIABLES_URL, body, httpOptions).pipe(
            map(res => new AllProjectVariables().deserialize(res)));
    }

    saveCurrentEnvironment(currentEnvironmentName: string): Observable<string> {
        let body = "";
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json'
            })
        };

        return this.http
            .put<string>(this.VARIABLES_URL+"/environment?currentEnvironment=" + encodeURIComponent(currentEnvironmentName), body, httpOptions);
    }
}
