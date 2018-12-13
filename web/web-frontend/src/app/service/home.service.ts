import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {Home} from "../model/home/home.model";
import {JsonUtil} from "../utils/json.util";


@Injectable()
export class HomeService {

    private HOME_URL = "/rest/home";

    constructor(private http: HttpClient) {}

    getHomePageModel(): Observable<Home> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .get<Home>(this.HOME_URL, httpOptions)
            .pipe(map(it => new Home().deserialize(it)));
    }

    createProject(project: Project): Observable<Project> {
        const body = project.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.HOME_URL, body, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }
}
