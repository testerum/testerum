import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {ManualTestPlans} from "../plans/model/manual-test-plans.model";
import {Observable} from "rxjs";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestPlan} from "../plans/model/manual-test-plan.model";
import {FeatureService} from "../../../service/feature.service";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../model/feature/tree/root-feature-node.model";
import {map} from "rxjs/operators";
import {ManualTestsStatusTreeRoot} from "../plans/model/status-tree/manual-tests-status-tree-root.model";
import {ManualTest} from "../plans/model/manual-test.model";
import {ManualTreeStatusFilterModel} from "../common/manual-tests-status-tree/model/filter/manual-tree-status-filter.model";
import {Location} from "@angular/common";

@Injectable()
export class ManualTestPlansService {

    private readonly baseUrl: string;

    constructor(private http: HttpClient,
                private featureService:FeatureService,
                location: Location) {
        this.baseUrl = location.prepareExternalUrl("/rest/manual");
    }

    getExecPlans(): Observable<ManualTestPlans> {
        return this.http
            .get<ManualTestPlans>(this.baseUrl + "/plans")
            .pipe(map(it => {return new ManualTestPlans().deserialize(it)}));
    }

    getManualExecPlan(planPath: Path): Observable<ManualTestPlan> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .get<ManualTestPlan>(this.baseUrl + "/plans", httpOptions)
            .pipe(map(it => {return new ManualTestPlan().deserialize(it)}));
    }

    getAllManualTests(): Observable<RootFeatureNode> {
        let featuresTreeFilter = new FeaturesTreeFilter();
        featuresTreeFilter.includeAutomatedTests = false;
        featuresTreeFilter.includeEmptyFeatures = false;
        return this.featureService.getFeatureTree(featuresTreeFilter)
    }

    deleteManualExecPlan(planPath: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .delete<void>(this.baseUrl + "/plans", httpOptions);
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
            .post<ManualTestPlan>(this.baseUrl + "/status_tree", body, httpOptions)
            .pipe(map(it => {return new ManualTestsStatusTreeRoot().deserialize(it)}));
    }

    getManualTest(planPath: Path, testPath: Path): Observable<ManualTest> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
                .append('testPath', testPath.toString())
        };

        return this.http
            .get<ManualTestPlan>(this.baseUrl + "/plans/runner", httpOptions)
            .pipe(map(it => {return new ManualTest().deserialize(it)}));
    }

    save(manualExecPlan: ManualTestPlan): Observable<ManualTestPlan>  {
        let body = manualExecPlan.serialize();
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };

        return this.http
            .put<ManualTestPlan>(this.baseUrl + "/plans", body, httpOptions).pipe(
                map(res => new ManualTestPlan().deserialize(res)));
    }

    finalizeManualExecPlan(planPath: Path): Observable<ManualTestPlan> {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .get<ManualTestPlan>(this.baseUrl + "/plans/finalize", httpOptions)
            .pipe(map(it => {return new ManualTestPlan().deserialize(it)}));
    }

    activatePlan(planPath: Path) {
        const httpOptions = {
            params: new HttpParams()
                .append('planPath', planPath.toString())
        };

        return this.http
            .get<ManualTestPlan>(this.baseUrl + "/plans/activate", httpOptions)
            .pipe(map(it => {return new ManualTestPlan().deserialize(it)}));
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
            .put<ManualTest>(this.baseUrl + "/plans/runner", body, httpOptions).pipe(
                map(res => new ManualTest().deserialize(res)));
    }

    getPathOfUnExecutedTest(planPath: Path, currentTestPath: string): Observable<Path> {

        let httpParams = new HttpParams()
            .append('planPath', planPath.toString())
            .append('currentTestPath', currentTestPath)

        const httpOptions = {
            params: httpParams
        };

        return this.http
            .get<string>(this.baseUrl + "/plans/runner/next", httpOptions)
            .pipe(map(it => {return Path.deserialize(it)}));
    }


}
