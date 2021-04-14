import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Project} from "../model/home/project.model";
import {CreateProjectRequest} from "../model/home/create-project-request.model";
import {Location} from "@angular/common";

@Injectable()
export class ProjectService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/projects");
    }

    getAllProjects(): Observable<Array<Project>> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .get<Array<Project>>(this.baseUrl, httpOptions)
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
            .post<any>(this.baseUrl, body, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }

    openProject(selectedPathAsString: string): Observable<Project> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', selectedPathAsString)
        };

        return this.http
            .post<any>(this.baseUrl+"/open", null, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }

    deleteProject(projectPathAsString: string): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', projectPathAsString)
        };

        return this.http
            .delete<any>(this.baseUrl+"/recent", httpOptions);
    }

    renameProject(renameProject: Project): Observable<Project> {
        const body = renameProject.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<any>(this.baseUrl+"/rename", body, httpOptions)
            .pipe(map(it => Project.deserialize(it)));
    }
}
