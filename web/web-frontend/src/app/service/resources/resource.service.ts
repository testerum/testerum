import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers, URLSearchParams} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {ResourceContext} from "../../model/resource/resource-context.model";
import {RdbmsConnectionResourceType} from "../../functionalities/resources/tree/model/type/rdbms-connection.resource-type.model";
import {Path} from "../../model/infrastructure/path/path.model";
import {Subject} from "rxjs/Subject";
import {FormValidationModel} from "../../model/exception/form-validation.model";
import {RenamePath} from "../../model/infrastructure/path/rename-path.model";
import {CopyPath} from "../../model/infrastructure/path/copy-path.model";
import {Router} from "@angular/router";
import {ErrorService} from "../error.service";

@Injectable()
export class ResourceService {

    private RESOURCES_URL = "/rest/resources";

    constructor(private router: Router,
                private http: Http,
                private errorService: ErrorService) {
    }

    isThereAnyResourceAtPath(path: Path):Observable<boolean> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', path.toString());

        return this.http
            .get(this.RESOURCES_URL + "/exist", {search: params})
            .map((response: Response, index: number) => ResourceService.extractBoolean(response))
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }
    private static extractBoolean(res: Response): boolean {
        return res.json();
    }

    /**
     * The second parameter is an optional parameter.
     * Needs to be provided only if the resource body is an instance of Serializable (custom object)*/
    getResource(path: Path, resourceBodyInstanceForDeserialization?: Serializable<any>): Observable<ResourceContext<any>> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', path.toString());

        return this.http
            .get(this.RESOURCES_URL, {search: params})
            .map((response: Response, index: number) => ResourceService.extractResource(response, resourceBodyInstanceForDeserialization))
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    deleteResource(path: Path): Observable<void> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', path.toString());

        return this.http
            .delete(this.RESOURCES_URL, {search: params})
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    /**
     * The second parameter is an optional parameter.
     * Needs to be provided only if the resource body is an instance of Serializable (custom object)*/
    saveResource(resource: ResourceContext<any>, resourceBodyInstanceForDeserialization?: Serializable<any>): Observable<any> {

        if (resource.body.hasOwnProperty('name')) {
            this.setNameToPath(resource)
        }

        let body = resource.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<ResourceContext<any>> = new Subject<ResourceContext<any>>();

        this.http
            .post(this.RESOURCES_URL, body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        ResourceService.extractResource(response, resourceBodyInstanceForDeserialization)
                    )
                },
                (err) => {
                    if (err.status === 400) {
                        let json = JSON.parse(err.text());

                        if (json['errorCode'] == "VALIDATION") {
                            responseSubject.error(
                                FormValidationModel.createaInstanceFromJson(json["validationModel"])
                            )
                        }

                    } else {
                        this.errorService.handleHttpResponseException(err)
                    }
                }
            );

        return responseSubject.asObservable();
    }

    private setNameToPath(resource: ResourceContext<any>) {
        let oldPath = resource.path;
        resource.path = new Path(oldPath.directories, resource.body.name, RdbmsConnectionResourceType.getInstanceForRoot().fileExtension);
    }

    private static extractResource(res: Response, resourceBodyInstance?: Serializable<any>): ResourceContext<any> {

        let resource = new ResourceContext<any>(resourceBodyInstance).deserialize(res.json());
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
            .get(this.RESOURCES_URL + "/shared/paths/" + resourceType)
            .map(ResourceService.extractPaths)
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    renameDirectory(renamePath: RenamePath): Observable<Path> {

        let body = renamePath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .put(this.RESOURCES_URL + "/directory", body, options)

            .map((response: Response, index: number) => {
                let json = response.json();
                return Path.deserialize(json);
            })
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', pathToDelete.toString());

        return this.http
            .delete(this.RESOURCES_URL + "/directory", {search: params})
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {

        let body = copyPath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .post(this.RESOURCES_URL + "/directory/move", body, options)
            .catch(err => {
                return this.errorService.handleHttpResponseException(err)
            });
    }

    private static extractPaths(res: Response): Array<Path> {
        let json = res.json();

        let response: Array<Path> = [];

        for (let pathAsString of json) {
            response.push(
                Path.createInstance(pathAsString)
            )
        }

        return response;
    }

    showResourcesScreen() {
        this.router.navigate(["automated/resources"]);
    }


}
