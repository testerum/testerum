import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import {TestModel} from "../model/test/test.model";
import {Path} from "../model/infrastructure/path/path.model";
import {CopyPath} from "../model/infrastructure/path/copy-path.model";
import {UpdateTestModel} from "../model/test/operation/update-test.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {UrlService} from "./url.service";


@Injectable()
export class TestsService {

    private TESTS_URL = "/rest/tests";

    constructor(private http: HttpClient,
                private urlService: UrlService) {}

    runTest(testModel:TestModel): Observable<void> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<void>(this.TESTS_URL+"/run/unsaved", body, httpOptions);
    }

    delete(testModel:TestModel): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', testModel.path.toString())
        };

        return this.http
            .delete<void>(this.TESTS_URL, httpOptions);
    }

    createTest(testModel:TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.TESTS_URL + "/create", body, httpOptions)
            .map(TestsService.extractTestModel);
    }

    updateTest(updateTestModel:UpdateTestModel): Observable<TestModel> {
        let body = updateTestModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.TESTS_URL + "/update", body, httpOptions)
            .map(TestsService.extractTestModel);
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
            .get<TestModel>(this.TESTS_URL, httpOptions)
            .map(TestsService.extractTestModel);
    }

    private static extractTestModel(res: TestModel):TestModel {
        return new TestModel().deserialize(res);
    }

    deleteDirectory(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<void>(this.TESTS_URL + "/directory", httpOptions);
    }

    showTestsScreen() {
        this.urlService.navigateToFeatures();
    }

    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {
        let body = copyPath.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<void>(this.TESTS_URL + "/directory/move", body, httpOptions);
    }

    getAllAutomatedTestsUnderContainer(path: Path): Observable<Array<TestModel>>  {

        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<Array<TestModel>>(this.TESTS_URL + "/automated/under-path", httpOptions)
            .map(TestsService.extractTestsModel);
    }

    getWarnings(testModel:TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.TESTS_URL + "/warnings", body, httpOptions)
            .map(TestsService.extractTestModel);
    }

}
