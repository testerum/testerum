import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Variable} from "../model/variable/variable.model";
import {JsonUtil} from "../utils/json.util";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable()
export class VariablesService {

    private VARIABLES_URL = "/rest/variables";

    constructor(private http: HttpClient) {}


    getVariables(): Observable<Array<Variable>> {
        const httpOptions = {
            headers: new HttpHeaders({
                'X-Testerum-Project':  '/media/cvmocanu/data/Development/testerum/testerum-demos/pet-clinic-angular2/tests', // todo: replace this with the actual project path
            })
        };

        return this.http
            .get<Array<Variable>>(this.VARIABLES_URL, httpOptions).pipe(
            map(VariablesService.extractVariablesModel));
    }

    save(model: Array<Variable>): Observable<Array<Variable>> {
        let body = JsonUtil.serializeArray(model);
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
                'X-Testerum-Project':  '/media/cvmocanu/data/Development/testerum/testerum-demos/pet-clinic-angular2/tests', // todo: replace this with the actual project path
            })
        };

        return this.http
            .post<Array<Variable>>(this.VARIABLES_URL, body, httpOptions).pipe(
            map(VariablesService.extractVariablesModel));
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
