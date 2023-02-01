import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";
import {Location} from "@angular/common";

@Injectable()
export class DemoService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/demo");
    }

    openDemoProject(): Observable<Project> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.baseUrl+"/start", null, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }
}
