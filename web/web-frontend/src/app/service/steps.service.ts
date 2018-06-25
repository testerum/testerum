import {Injectable} from '@angular/core';
import {BasicStepDef} from "../model/basic-step-def.model";
import {Http, RequestOptions, Response, Headers, URLSearchParams} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {ComposedStepDef} from "../model/composed-step-def.model";
import {ErrorService} from "./error.service";
import {RenamePath} from "../model/infrastructure/path/rename-path.model";
import {Path} from "../model/infrastructure/path/path.model";
import {Router} from "@angular/router";
import {Subject} from "rxjs/Subject";
import {CopyPath} from "../model/infrastructure/path/copy-path.model";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";
import {CheckComposedStepDefUpdateCompatibilityResponse} from "../model/step/CheckComposedStepDefUpdateCompatibilityResponse";
import {UpdateComposedStepDef} from "../model/step/UpdateComposedStepDef";

@Injectable()
export class StepsService {

    private BASIC_STEPS_URL = "/rest/steps/basic";
    private COMPOSED_STEPS_URL = "rest/steps/composed";

    constructor(private router: Router,
                private http: Http,
                private errorService: ErrorService  ) {
    }

    getComposedStepDef(pathAsString: string): Observable<ComposedStepDef> {
        return this.http
            .get(this.COMPOSED_STEPS_URL, {params: {path: pathAsString}})
            .map(StepsService.extractComposedStepDef)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getComposedStepDefs(): Observable<Array<ComposedStepDef>> {
        return this.http
            .get(this.COMPOSED_STEPS_URL)
            .map(StepsService.extractComposedStepDefs)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    deleteComposedStepsDef(model: ComposedStepDef): Observable<void> {
        return this.http
            .delete(this.COMPOSED_STEPS_URL, {params: {path: model.path.toString()}})
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    checkComposedStepDefUpdate(model: UpdateComposedStepDef): Observable<CheckComposedStepDefUpdateCompatibilityResponse> {
        let body = model.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<CheckComposedStepDefUpdateCompatibilityResponse> = new Subject<CheckComposedStepDefUpdateCompatibilityResponse>();
        this.http
            .post(this.COMPOSED_STEPS_URL + "/update/check", body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        StepsService.extractCheckComposedStepDefUpdateCompatibilityResponse(response)
                    )
                },
                (err) => {
                    this.errorService.handleHttpResponseException(err)
                }
            );

        return responseSubject.asObservable();
    }

    createComposedStepDef(model: ComposedStepDef): Observable<ComposedStepDef> {
        let body = model.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<ComposedStepDef> = new Subject<ComposedStepDef>();
        this.http
            .post(this.COMPOSED_STEPS_URL + "/create", body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        StepsService.extractComposedStepDef(response)
                    )
                },
                (err) => {
                    if (err.status === 400) {
                        let json = JSON.parse(err.text());

                        if (json['errorCode'] == "VALIDATION") {
                            let validationError = new ValidationErrorResponse().deserialize(json);
                            responseSubject.error(
                                validationError
                            )
                        }

                    } else {
                        this.errorService.handleHttpResponseException(err)
                    }
                }
            );

        return responseSubject.asObservable();
    }

    updateComposedStepDef(model: ComposedStepDef): Observable<ComposedStepDef> {
        let body = model.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<ComposedStepDef> = new Subject<ComposedStepDef>();
        this.http
            .post(this.COMPOSED_STEPS_URL + "/update", body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        StepsService.extractComposedStepDef(response)
                    )
                },
                (err) => {
                    if (err.status === 400) {
                        let json = JSON.parse(err.text());

                        if (json['errorCode'] == "VALIDATION") {
                            let validationError = new ValidationErrorResponse().deserialize(json);
                            responseSubject.error(
                                validationError
                            )
                        }

                    } else {
                        this.errorService.handleHttpResponseException(err)
                    }
                }
            );

        return responseSubject.asObservable();

    }

    getDefaultSteps(): Observable<Array<BasicStepDef>> {
        return this.http
            .get(this.BASIC_STEPS_URL)
            .map(StepsService.extractBasicStepsDef)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractComposedStepDefs(res: Response): Array<ComposedStepDef> {
        let json = res.json();

        let response: Array<ComposedStepDef> = [];
        for (let composedStepAsJson of json) {
            let composedStepDef = new ComposedStepDef().deserialize(composedStepAsJson);
            response.push(composedStepDef)
        }

        return response;
    }

    private static extractComposedStepDef(res: Response): ComposedStepDef {
        let json = res.json();

        return new ComposedStepDef().deserialize(json);
    }

    private static extractCheckComposedStepDefUpdateCompatibilityResponse(res: Response): CheckComposedStepDefUpdateCompatibilityResponse {
        let json = res.json();

        return new CheckComposedStepDefUpdateCompatibilityResponse().deserialize(json);
    }

    private static extractBasicStepsDef(res: Response): Array<BasicStepDef> {
        let json = res.json();

        let response: Array<BasicStepDef> = [];
        for (let basicStepModelAsJson of json) {
            let basicStepModel = new BasicStepDef().deserialize(basicStepModelAsJson);
            response.push(basicStepModel)
        }

        return response;
    }

    renameDirectory(renamePath: RenamePath): Observable<Path> {

        let body = renamePath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .put(this.COMPOSED_STEPS_URL + "/directory", body, options)

            .map((response: Response, index: number) => {
                let json = response.json();
                return Path.deserialize(json);
            })
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('path', pathToDelete.toString());

        return this.http
            .delete(this.COMPOSED_STEPS_URL + "/directory", {search: params})
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    showStepsScreen() {
        this.router.navigate(["automated/steps"]);
    }


    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {

        let body = copyPath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .post(this.COMPOSED_STEPS_URL + "/directory/move", body, options)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getBasicStepDef(pathAsString: string): Observable<ComposedStepDef> {
        return this.http
            .get(this.BASIC_STEPS_URL, {params: {path: pathAsString}})
            .map(StepsService.extractBasicStepDef)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractBasicStepDef(res: Response): BasicStepDef {
        let json = res.json();

        return new BasicStepDef().deserialize(json);
    }
}
