
import {Injectable} from "@angular/core";
import {Http, RequestOptions, Response, Headers} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import {Router} from "@angular/router";
import {ErrorService} from "../../../service/error.service";
import {ManualTestModel} from "../../model/manual-test.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {UpdateManualTestModel} from "../../model/operation/update-manual-test.model";
import {RenamePath} from "../../../model/infrastructure/path/rename-path.model";
import {CopyPath} from "../../../model/infrastructure/path/copy-path.model";
import {ArrayUtil} from "../../../utils/array.util";


@Injectable()
export class ManualTestsService {

    private TESTS_URL = "/rest/manualTests";

    static manualTestsTags:Array<string> = [];

    constructor(private router: Router,
                private http:Http,
                private errorService: ErrorService) {
    }

    delete(testModel:ManualTestModel): Observable<void> {
        return this.http
                .delete(this.TESTS_URL, {params: {path: testModel.path.toString()}})
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    createTest(testModel:ManualTestModel): Observable<ManualTestModel> {
        let body = testModel.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
                .post(this.TESTS_URL + "/create", body, options)
                .map(ManualTestsService.extractTestModel)
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    updateTest(updateManualTestModel:UpdateManualTestModel): Observable<ManualTestModel> {
        let body = updateManualTestModel.serialize();
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http
                .post(this.TESTS_URL + "/update", body, options)
                .map(ManualTestsService.extractTestModel)
                .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    getTests(): Observable<Array<ManualTestModel>> {
        return this.http
            .get(this.TESTS_URL)
            .map(ManualTestsService.extractTestsModel)
            .catch(err => {return this.errorService.handleHttpResponseException(err)});
    }

    private static extractTestsModel(res: Response):Array<ManualTestModel> {
        let json = res.json();

        let response:Array<ManualTestModel> = [];
        for(let testsModelAsJson of json) {
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
        return this.http
            .get(this.TESTS_URL, {params: {path: pathAsString}})
            .map(ManualTestsService.extractTestModel)
            .catch(err => {return this.errorService.handleHttpResponseException(err)} );
    }

    private static extractTestModel(res: Response):ManualTestModel {
        let json = res.json();
        return new ManualTestModel().deserialize(json);
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
