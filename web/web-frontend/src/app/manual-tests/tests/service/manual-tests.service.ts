import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {Router} from "@angular/router";
import {ManualTestModel} from "../../model/manual-test.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UpdateManualTestModel} from "../../model/operation/update-manual-test.model";
import {RenamePath} from "../../../model/infrastructure/path/rename-path.model";
import {CopyPath} from "../../../model/infrastructure/path/copy-path.model";
import {ArrayUtil} from "../../../utils/array.util";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";


@Injectable()
export class ManualTestsService {

    private TESTS_URL = "/rest/manualTests";

    static manualTestsTags:Array<string> = [];

    constructor(private router: Router,
                private http: HttpClient) {
    }

    delete(testModel:ManualTestModel): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', testModel.path.toString())
        };

        return this.http
            .delete<void>(this.TESTS_URL, httpOptions);
    }

    createTest(testModel:ManualTestModel): Observable<ManualTestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ManualTestModel>(this.TESTS_URL + "/create", body, httpOptions).pipe(
            map(res => new ManualTestModel().deserialize(res)));
    }

    updateTest(updateManualTestModel:UpdateManualTestModel): Observable<ManualTestModel> {
        let body = updateManualTestModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<ManualTestModel>(this.TESTS_URL + "/update", body, httpOptions).pipe(
            map(res => new ManualTestModel().deserialize(res)));
    }

    getTests(): Observable<Array<ManualTestModel>> {
        return this.http
            .get<Array<ManualTestModel>>(this.TESTS_URL).pipe(
            map(ManualTestsService.extractTestsModel));
    }

    private static extractTestsModel(res: Array<ManualTestModel>):Array<ManualTestModel> {
        let response:Array<ManualTestModel> = [];
        for(let testsModelAsJson of res) {
            let testsModel = new ManualTestModel().deserialize(testsModelAsJson);
            response.push(testsModel)
        }

        ManualTestsService.setManualTestsTags(response);

        return response;
    }

    static setManualTestsTags(manualTests:Array<ManualTestModel>) {
        let tags:Array<string> = [];
        for (let manualTest of manualTests) {
            for (let tag of manualTest.tags) {
                if(!ArrayUtil.containsElement(tags, tag)) {
                    tags.push(tag)
                }
            }
        }
        ArrayUtil.sort(tags);
        ArrayUtil.replaceElementsInArray(ManualTestsService.manualTestsTags, tags)
    }

    getTest(pathAsString: string): Observable<ManualTestModel> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<ManualTestModel>(this.TESTS_URL, httpOptions).pipe(
            map(res => new ManualTestModel().deserialize(res)));
    }

    renameDirectory(renamePath: RenamePath): Observable<Path> {
        let body = renamePath.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<Path>(this.TESTS_URL + "/directory", body, httpOptions).pipe(
            map(res => Path.deserialize(res)));
    }

    deleteDirectory(pathToDelete: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', pathToDelete.toString())
        };

        return this.http
            .delete<void>(this.TESTS_URL + "/directory", httpOptions);
    }

    showTestsScreen() {
        this.router.navigate(["manual/tests"]);
    }

    moveDirectoryOrFile(copyPath: CopyPath): Observable<void> {
        let body = copyPath.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<void>(this.TESTS_URL + "/directory/move", body, httpOptions)
    }
}
