import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';


import {ResourceContext} from "../../model/resource/resource-context.model";
import {RdbmsConnectionResourceType} from "../../functionalities/resources/tree/model/type/rdbms-connection.resource-type.model";
import {Path} from "../../model/infrastructure/path/path.model";
import {RenamePath} from "../../model/infrastructure/path/rename-path.model";
import {CopyPath} from "../../model/infrastructure/path/copy-path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {UrlService} from "../url.service";

@Injectable()
export class ResourceService {

    private RESOURCES_URL = "/rest/resources";

    constructor(private urlService: UrlService,
                private http: HttpClient) {
    }

    /**
     * The second parameter is an optional parameter.
     * Needs to be provided only if the resource body is an instance of Serializable (custom object)*/
    getResource(path: Path, resourceBodyInstanceForDeserialization?: Serializable<any>): Observable<ResourceContext<any>> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<ResourceContext<any>>(this.RESOURCES_URL, httpOptions).pipe(
            map((res: ResourceContext<any>) => ResourceService.extractResource(res, resourceBodyInstanceForDeserialization)));
    }

    deleteResource(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<void>(this.RESOURCES_URL, httpOptions);
    }

    /**
     * The second parameter is an optional parameter.
     * Needs to be provided only if the resource body is an instance of Serializable (custom object)*/
    saveResource(resource: ResourceContext<any>, resourceBodyInstanceForDeserialization?: Serializable<any>): Observable<any> {

        if (resource.body.hasOwnProperty('name')) {
            this.setNameToPath(resource)
        }

        let body = resource.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<any>(this.RESOURCES_URL, body, httpOptions).pipe(
            map(res => ResourceService.extractResource(res, resourceBodyInstanceForDeserialization)));
    }

    private setNameToPath(resource: ResourceContext<any>) {
        let oldPath = resource.path;
        resource.path = new Path(oldPath.directories, resource.body.name, RdbmsConnectionResourceType.getInstanceForRoot().fileExtension);
    }

    private static extractResource(res: ResourceContext<any>, resourceBodyInstance?: Serializable<any>): ResourceContext<any> {
        let resource = new ResourceContext<any>(resourceBodyInstance).deserialize(res);
        if (resource.body.hasOwnProperty("name")) {
            ResourceService.setNameFromPath(resource)
        }

        return resource
    }

    private static setNameFromPath(resource: ResourceContext<any>) {
        resource.body.name = resource.path.fileName;
    }

    getResourcePaths(resourceType: string): Observable<Array<Path>> {
        return this.http
            .get<Array<string>>(this.RESOURCES_URL + "/shared/paths/" + resourceType).pipe(
            map(ResourceService.extractPaths));
    }

    private static extractPaths(res: Array<string>): Array<Path> {
        let response: Array<Path> = [];
        for (let pathAsString of res) {
            response.push(
                Path.createInstance(pathAsString)
            )
        }

        return response;
    }

    renameDirectory(renamePath: RenamePath): Observable<Path> {
        let body = renamePath.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<Path>(this.RESOURCES_URL + "/directory", body, httpOptions).pipe(
            map(res => Path.deserialize(res)));
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathToDelete.toString())
        };

        return this.http
            .delete<void>(this.RESOURCES_URL + "/directory", httpOptions);
    }

    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {
        let body = copyPath.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<void>(this.RESOURCES_URL + "/directory/move", body, httpOptions);
    }

    showResourcesScreen() {
        this.urlService.navigateToResources();
    }
}
