import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Variable} from "../functionalities/variables/model/variable.model";
import {JsonUtil} from "../utils/json.util";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ProjectVariables} from "../functionalities/variables/model/project-variables.model";

@Injectable()
export class VariablesService {

    private VARIABLES_URL = "/rest/variables";

    constructor(private http: HttpClient) {}

    getVariables(): Observable<ProjectVariables> {
        return this.http
            .get<ProjectVariables>(this.VARIABLES_URL).pipe(
            map(res => new ProjectVariables().deserialize(res)));
    }

    save(model: ProjectVariables): Observable<ProjectVariables> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json'
            })
        };

        return this.http
            .post<ProjectVariables>(this.VARIABLES_URL, body, httpOptions).pipe(
            map(res => new ProjectVariables().deserialize(res)));
    }
}
