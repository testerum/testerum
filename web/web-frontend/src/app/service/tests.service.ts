import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {TestModel} from "../model/test/test.model";
import {Path} from "../model/infrastructure/path/path.model";
import {CopyPath} from "../model/infrastructure/path/copy-path.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {UrlService} from "./url.service";


@Injectable()
export class TestsService {

    private TESTS_URL = "/rest/tests";

    constructor(private http: HttpClient,
                private urlService: UrlService) {}

    delete(testModel:TestModel): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', testModel.path.toString())
        };

        return this.http
            .delete<void>(this.TESTS_URL, httpOptions);
    }

    saveTest(testModel: TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.TESTS_URL + "/save", body, httpOptions).pipe(
            map(TestsService.extractTestModel));
    }

    private static extractTestsModel(res: Array<TestModel>):Array<TestModel> {
        let response:Array<TestModel> = [];
        for(let testsModelAsJson of res) {
            let testsModel = new TestModel().deserialize(testsModelAsJson);
            response.push(testsModel)
        }

        return response;
    }

    getTest(pathAsString: string): Observable<TestModel> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<TestModel>(this.TESTS_URL, httpOptions).pipe(
            map(TestsService.extractTestModel));
    }

    private static extractTestModel(res: TestModel):TestModel {
        return new TestModel().deserialize(res);
    }

    showTestsScreen() {
        this.urlService.navigateToFeatures();
    }

    getWarnings(testModel:TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.TESTS_URL + "/warnings", body, httpOptions).pipe(
            map(TestsService.extractTestModel));
    }

}
