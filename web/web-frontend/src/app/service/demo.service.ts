import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";

@Injectable()
export class DemoService {

    private BASE_URL = "/rest/demo";

    constructor(private http: HttpClient) {}

    openDemoProject(): Observable<Project> {

        return this.http
            .post<any>(this.BASE_URL+"/start", null, null)
            .pipe(map(it => Project.deserialize(it)));
    }
}
