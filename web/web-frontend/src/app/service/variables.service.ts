import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AllProjectVariables} from "../functionalities/variables/model/project-variables.model";
import {Location} from "@angular/common";

@Injectable()
export class VariablesService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/variables");
    }

    getVariables(): Observable<AllProjectVariables> {
        return this.http
            .get<AllProjectVariables>(this.baseUrl).pipe(
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
            .post<AllProjectVariables>(this.baseUrl, body, httpOptions).pipe(
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
            .put<string>(this.baseUrl+"/environment?currentEnvironment=" + encodeURIComponent(currentEnvironmentName), body, httpOptions);
    }
}
