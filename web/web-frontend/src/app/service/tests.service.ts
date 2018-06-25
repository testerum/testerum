
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


@Injectable()
export class TestsService {

    private TESTS_URL = "/rest/tests";

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    runTest(testModel:TestModel): Observable<void> {
        let body = testModel.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
            .post(this.TESTS_URL+"/run/unsaved", body, options)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    delete(testModel:TestModel): Observable<void> {
        return this.http
                .delete(this.TESTS_URL, {params: {path: testModel.path.toString()}})
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    createTest(testModel:TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
                .post(this.TESTS_URL + "/create", body, options)
                .map(TestsService.extractTestModel)
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    updateTest(updateTestModel:UpdateTestModel): Observable<TestModel> {
        let body = updateTestModel.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
                .post(this.TESTS_URL + "/update", body, options)
                .map(TestsService.extractTestModel)
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getTests(): Observable<Array<TestModel>> {
        return this.http
            .get(this.TESTS_URL)
            .map(TestsService.extractTestsModel)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractTestsModel(res: Response):Array<TestModel> {
        let json = res.json();

        let response:Array<TestModel> = [];
        for(let testsModelAsJson of json) {
            let testsModel = new TestModel().deserialize(testsModelAsJson);
            response.push(testsModel)
        }

        return response;
    }

    getTest(pathAsString: string): Observable<TestModel> {
        return this.http
            .get(this.TESTS_URL, {params: {path: pathAsString}})
            .map(TestsService.extractTestModel)
            .catch(err => {return this.errorService.handleHttpResponseException(err)} );
    }

    private static extractTestModel(res: Response):TestModel {
        let json = res.json();
        return new TestModel().deserialize(json);
    }

    renameDirectory(renamePath: RenamePath): Observable<Path> {

        let body = renamePath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .put(this.TESTS_URL + "/directory", body, options)

            .map((response: Response, index: number) => {
                let json = response.json();
                return Path.deserialize(json);
            })
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        return this.http
            .delete(this.TESTS_URL + "/directory", {params: {path: pathToDelete.toString()}})
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    showTestsScreen() {
        this.router.navigate(["automated/tests"]);
    }

    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {

        let body = copyPath.serialize();
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http
            .post(this.TESTS_URL + "/directory/move", body, options)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }
}
