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

@Injectable()
export class VariablesService {

    private VARIABLES_URL = "/rest/variables";

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    getVariables(): Observable<Array<Variable>> {
        return this.http
            .get(this.VARIABLES_URL)
            .map(VariablesService.extractVariablesModel)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractVariablesModel(res: Response):Array<Variable> {
        let json = res.json();

        let response:Array<Variable> = [];
        for(let variableAsJson of json) {
            let testsModel = new Variable().deserialize(variableAsJson);
            response.push(testsModel)
        }
        response.push(new Variable());

        return response;
    }

    save(model: Array<Variable>): Observable<Array<Variable>> {
        let body = JsonUtil.serializeArray(model);
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        let responseSubject: Subject<Array<Variable>> = new Subject<Array<Variable>>();
        this.http
            .post(this.VARIABLES_URL, body, options)
            .subscribe(
                (response: Response) => {
                    responseSubject.next(
                        VariablesService.extractVariablesModel(response)
                    )
                },
                (err) => {
                    this.errorService.handleHttpResponseException(err)
                }
            );

        return responseSubject.asObservable();
    }
}
