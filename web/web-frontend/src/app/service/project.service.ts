import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";
import {Setting} from "../functionalities/config/settings/model/setting.model";
import {Home} from "../model/home/home.model";
import {JsonUtil} from "../utils/json.util";
import {TestModel} from "../model/test/test.model";


@Injectable()
export class ProjectService {

    private BASE_URL = "/rest/projects";

    constructor(private http: HttpClient) {}

    getAllProjects(): Observable<Array<Project>> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .get<Array<Project>>(this.BASE_URL, httpOptions)
            .pipe(map(it => ProjectService.extractProject(it)));
    }

    private static extractProject(res: Array<Project>):Array<Project> {
        let response:Array<Project> = [];
        for(let projectAsJson of res) {
            let project = Project.deserialize(projectAsJson);
            response.push(project)
        }

        return response;
    }

    createProject(project: Project): Observable<Project> {
        const body = project.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.BASE_URL, body, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }

    openProject(selectedPathAsString: string): Observable<Project> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', selectedPathAsString)
        };

        return this.http
            .post<any>(this.BASE_URL+"/open", null, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }
}
