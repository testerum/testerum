import {Injectable} from '@angular/core';
import {BasicStepDef} from "../model/basic-step-def.model";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {ComposedStepDef} from "../model/composed-step-def.model";
import {RenamePath} from "../model/infrastructure/path/rename-path.model";
import {Path} from "../model/infrastructure/path/path.model";
import {CopyPath} from "../model/infrastructure/path/copy-path.model";
import {CheckComposedStepDefUpdateCompatibilityResponse} from "../model/step/CheckComposedStepDefUpdateCompatibilityResponse";
import {UpdateComposedStepDef} from "../model/step/UpdateComposedStepDef";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {UrlService} from "./url.service";
import {StepsTreeFilter} from "../model/step/filter/steps-tree-filter.model";
import {FeatureService} from "./feature.service";
import {RootServerTreeNode} from "../model/tree/root-server-tree-node.model";

@Injectable()
export class StepsService {

    private BASIC_STEPS_URL = "/rest/steps/basic";
    private COMPOSED_STEPS_URL = "rest/steps/composed";

    constructor(private http: HttpClient,
                private urlService: UrlService) {}

    getComposedStepDef(pathAsString: string): Observable<ComposedStepDef> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<ComposedStepDef>(this.COMPOSED_STEPS_URL, httpOptions)
            .map(StepsService.extractComposedStepDef);
    }

    getComposedStepDefs(stepTreeFilter: StepsTreeFilter = new StepsTreeFilter()): Observable<Array<ComposedStepDef>> {
        let body = stepTreeFilter.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<RootServerTreeNode>(this.COMPOSED_STEPS_URL, body, httpOptions)
            .map(StepsService.extractComposedStepDefs);
    }

    deleteComposedStepsDef(model: ComposedStepDef): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', model.path.toString())
        };

        return this.http
            .delete<void>(this.COMPOSED_STEPS_URL, httpOptions);
    }

    checkComposedStepDefUpdate(model: UpdateComposedStepDef): Observable<CheckComposedStepDefUpdateCompatibilityResponse> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<CheckComposedStepDefUpdateCompatibilityResponse>(this.COMPOSED_STEPS_URL + "/update/check", body, httpOptions)
            .map(StepsService.extractCheckComposedStepDefUpdateCompatibilityResponse);
    }

    createComposedStepDef(model: ComposedStepDef): Observable<ComposedStepDef> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ComposedStepDef>(this.COMPOSED_STEPS_URL + "/create", body, httpOptions)
            .map(StepsService.extractComposedStepDef);
    }

    updateComposedStepDef(model: ComposedStepDef): Observable<ComposedStepDef> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ComposedStepDef>(this.COMPOSED_STEPS_URL + "/update", body, httpOptions)
            .map(StepsService.extractComposedStepDef);
    }

    getDefaultSteps(stepTreeFilter: StepsTreeFilter = new StepsTreeFilter()): Observable<Array<BasicStepDef>> {
        let body = stepTreeFilter.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<RootServerTreeNode>(this.BASIC_STEPS_URL, body, httpOptions)
            .map(StepsService.extractBasicStepsDef);
    }

    private static extractComposedStepDefs(res: Array<ComposedStepDef>): Array<ComposedStepDef> {
        let response: Array<ComposedStepDef> = [];
        for (let composedStepAsJson of res) {
            let composedStepDef = new ComposedStepDef().deserialize(composedStepAsJson);
            response.push(composedStepDef)
        }

        return response;
    }

    private static extractComposedStepDef(res: ComposedStepDef): ComposedStepDef {
        return new ComposedStepDef().deserialize(res);
    }

    private static extractCheckComposedStepDefUpdateCompatibilityResponse(res: CheckComposedStepDefUpdateCompatibilityResponse): CheckComposedStepDefUpdateCompatibilityResponse {
        return new CheckComposedStepDefUpdateCompatibilityResponse().deserialize(res);
    }

    private static extractBasicStepsDef(res: Array<BasicStepDef>): Array<BasicStepDef> {
        let response: Array<BasicStepDef> = [];
        for (let basicStepModelAsJson of res) {
            let basicStepModel = new BasicStepDef().deserialize(basicStepModelAsJson);
            response.push(basicStepModel)
        }

        return response;
    }

    renameDirectory(model: RenamePath): Observable<Path> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<Path>(this.COMPOSED_STEPS_URL + "/directory", body, httpOptions)
            .map(res => Path.deserialize(res));
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathToDelete.toString())
        };

        return this.http
            .delete<void>(this.COMPOSED_STEPS_URL + "/directory", httpOptions);
    }

    showStepsScreen() {
        this.urlService.navigateToSteps();
    }

    moveDirectoryOrFile(model: CopyPath): Observable<void> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<void>(this.COMPOSED_STEPS_URL + "/directory/move", body, httpOptions);
    }

    getBasicStepDef(pathAsString: string): Observable<BasicStepDef> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<BasicStepDef>(this.BASIC_STEPS_URL, httpOptions)
            .map(StepsService.extractBasicStepDef);
    }

    private static extractBasicStepDef(res: BasicStepDef): BasicStepDef {
        return new BasicStepDef().deserialize(res);
    }
}
