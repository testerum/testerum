import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ManualExecPlans} from "../plans/model/manual-exec-plans.model";
import {Observable} from "rxjs";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualExecPlan} from "../plans/model/manual-exec-plan.model";
import {FeatureService} from "../../../service/feature.service";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../model/feature/tree/root-feature-node.model";
import {map} from "rxjs/operators";
import {ManualTestsStatusTreeRoot} from "../plans/model/status-tree/manual-tests-status-tree-root.model";

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

    getManualExecPlan(path: Path): Observable<ManualExecPlan> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
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

    deleteManualExecPlan(path: Path): Observable<void> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .delete<void>(this.BASE_URL + "/plans", httpOptions);
    }

    getManualTestsStatusTree(path: Path): Observable<ManualTestsStatusTreeRoot> {
        const httpOptions = {
            params: new HttpParams()
                .append('path', path.toString())
        };

        return this.http
            .get<ManualExecPlan>(this.BASE_URL + "/status_tree", httpOptions)
            .pipe(map(it => {return new ManualTestsStatusTreeRoot().deserialize(it)}));
    }
}
