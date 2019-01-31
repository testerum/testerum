import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";
import {CreateProjectRequest} from "../model/home/create-project-request.model";


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

    createProject(createProjectRequest: CreateProjectRequest): Observable<Project> {
        const body = createProjectRequest.serialize();
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

    deleteProject(projectPathAsString: string): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', projectPathAsString)
        };

        return this.http
            .delete<any>(this.BASE_URL+"/recent", httpOptions);
    }
}
