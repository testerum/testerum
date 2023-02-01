import {map} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs';
import {TestModel} from "../model/test/test.model";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {UrlService} from "./url.service";
import {ModelRepairerService} from "./model-repairer/model-repairer.service";
import {Location} from "@angular/common";

@Injectable()
export class TestsService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                private urlService: UrlService,
                private modelRepairer: ModelRepairerService,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/tests");
    }

    delete(testModel:TestModel): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', testModel.path.toString())
        };

        return this.http
            .delete<void>(this.baseUrl, httpOptions);
    }

    saveTest(testModel: TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.baseUrl + "/save", body, httpOptions).pipe(
            map(TestsService.extractTestModel),
            map(it => this.modelRepairer.repairTestModel(it)));
    }

    getTest(pathAsString: string): Observable<TestModel> {

        const httpOptions = {
            params: new HttpParams()
                .append('path', pathAsString)
        };

        return this.http
            .get<TestModel>(this.baseUrl, httpOptions).pipe(
            map(TestsService.extractTestModel),
            map(it => this.modelRepairer.repairTestModel(it)));
    }

    private static extractTestModel(res: TestModel):TestModel {
        return new TestModel().deserialize(res);
    }

    showTestsScreen() {
        this.urlService.navigateToProject();
    }

    getWarnings(testModel:TestModel): Observable<TestModel> {
        let body = testModel.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .post<TestModel>(this.baseUrl + "/warnings", body, httpOptions).pipe(
            map(TestsService.extractTestModel),
            map(it => this.modelRepairer.repairTestModel(it)));
    }
}
