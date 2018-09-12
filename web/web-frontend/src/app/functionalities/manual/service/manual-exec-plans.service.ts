import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {ManualExecPlans} from "../plans/model/manual-exec-plans.model";
import {Observable} from "rxjs";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualExecPlan} from "../plans/model/manual-exec-plan.model";
import {FeatureService} from "../../../service/feature.service";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../model/feature/tree/root-feature-node.model";
import {map} from "rxjs/operators";
import {ManualTestsStatusTreeRoot} from "../plans/model/status-tree/manual-tests-status-tree-root.model";
import {ManualTest} from "../plans/model/manual-test.model";
import {Feature} from "../../../model/feature/feature.model";
import {ManualTreeStatusFilterModel} from "../common/manual-tests-status-tree/model/filter/manual-tree-status-filter.model";

@Injectable()
export class ManualExecPlansService {

    private BASE_URL = "/rest/manual";

    constructor(private http: HttpClient,
                private featureService:FeatureService) {
    }

    getExecPlans(): Observable<ManualExecPlans> {
        return this.http
            .get<ManualExecPlans>(this.BASE_URL + "/plans")
            .pipe(map(it => {return new ManualExecPlans().deserialize(it)}));
    }

    getManualExecPlan(planPath: Path): Observable<ManualExecPlan> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .get<ManualExecPlan>(this.BASE_URL + "/plans", httpOptions)
            .pipe(map(it => {return new ManualExecPlan().deserialize(it)}));
    }

    getAllManualTests(): Observable<RootFeatureNode> {
        let featuresTreeFilter = new FeaturesTreeFilter();
        featuresTreeFilter.showAutomatedTests = false;
        featuresTreeFilter.showEmptyFeatures = false;
        return this.featureService.getFeatureTree(featuresTreeFilter)
    }

    deleteManualExecPlan(planPath: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .delete<void>(this.BASE_URL + "/plans", httpOptions);
    }

    getManualTestsStatusTree(planPath: Path, filter: ManualTreeStatusFilterModel): Observable<ManualTestsStatusTreeRoot> {
        let body = filter.serialize();

        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            }),
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .post<ManualExecPlan>(this.BASE_URL + "/status_tree", body, httpOptions)
            .pipe(map(it => {return new ManualTestsStatusTreeRoot().deserialize(it)}));
    }

    getManualTest(planPath: Path, testPath: Path): Observable<ManualTest> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
                .append('testPath', testPath.toString())
        };

        return this.http
            .get<ManualExecPlan>(this.BASE_URL + "/plans/runner", httpOptions)
            .pipe(map(it => {return new ManualTest().deserialize(it)}));
    }

    updateTestRun(planPath: Path, model: ManualTest): Observable<ManualTest> {
        let body = model.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            }),
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .put<ManualTest>(this.BASE_URL + "/plans/runner", body, httpOptions).pipe(
                map(res => new ManualTest().deserialize(res)));
    }

    getPathOfUnExecutedTest(planPath: Path, currentTestPath: Path): Observable<Path> {

        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
                .append('currentTestPath', currentTestPath.toString())
        };

        return this.http
            .get<string>(this.BASE_URL + "/plans/runner/next", httpOptions)
            .pipe(map(it => {return Path.deserialize(it)}));
    }
}
