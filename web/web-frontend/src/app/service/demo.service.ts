import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";

@Injectable()
export class DemoService {

    private BASE_URL = "/rest/demo";

    constructor(private http: HttpClient) {}

    openDemoProject(): Observable<Project> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL+"/start", null, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }
}
