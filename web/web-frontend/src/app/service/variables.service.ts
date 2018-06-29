import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import {TestModel} from "../model/test/test.model";
import {TestWebSocketService} from "./test-web-socket.service";
import {ErrorService} from "./error.service";
import {RenamePath} from "../model/infrastructure/path/rename-path.model";
import {Path} from "../model/infrastructure/path/path.model";
import {Router} from "@angular/router";
import {CopyPath} from "../model/infrastructure/path/copy-path.model";
import {UpdateTestModel} from "../model/test/operation/update-test.model";
import {Variable} from "../model/variable/variable.model";
import {ComposedStepDef} from "../model/composed-step-def.model";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";
import {StepsService} from "./steps.service";
import {JsonUtil} from "../utils/json.util";
import {Subject} from "rxjs/Rx";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {FeatureService} from "./feature.service";
import {RootServerTreeNode} from "../model/tree/root-server-tree-node.model";
import {Feature} from "../model/feature/feature.model";

@Injectable()
export class VariablesService {

    private VARIABLES_URL = "/rest/variables";

    constructor(private http: HttpClient) {}


    getVariables(): Observable<Array<Variable>> {
        return this.http
            .get<Array<Variable>>(this.VARIABLES_URL)
            .map(VariablesService.extractVariablesModel);
    }

    save(model: Array<Variable>): Observable<Array<Variable>> {
        let body = JsonUtil.serializeArray(model);
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<Array<Variable>>(this.VARIABLES_URL, body, httpOptions)
            .map(VariablesService.extractVariablesModel);
    }

    private static extractVariablesModel(res: Array<Variable>):Array<Variable> {
        let response:Array<Variable> = [];
        for(let variableAsJson of res) {
            let testsModel = new Variable().deserialize(variableAsJson);
            response.push(testsModel)
        }
        response.push(new Variable());

        return response;
    }
}
