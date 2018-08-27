import {Component, OnDestroy, OnInit} from '@angular/core';
import {ManualExecPlan} from "../model/manual-exec-plan.model";
import {ManualExecPlansService} from "../../service/manual-exec-plans.service";
import {ManualExecPlans} from "../model/manual-exec-plans.model";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../service/url.service";

@Component({
    selector: 'manual-exec-plans-overview',
    templateUrl: 'manual-exec-plans-overview.component.html',
    styleUrls: ['manual-exec-plans-overview.component.scss']
})
export class ManualExecPlansOverviewComponent implements OnInit, OnDestroy {

    activeExecPlans: Array<ManualExecPlan> = [];
    finalizedExecPlans: Array<ManualExecPlan> = [];

    getExecPlansSubscription: Subscription;
    constructor(private manualExecPlansService: ManualExecPlansService,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.getExecPlansSubscription = this.manualExecPlansService.getExecPlans().subscribe((manualExecPlans: ManualExecPlans) => {
            let serverActiveExecPlans = manualExecPlans.activeExecPlans;
            this.sortByCreateDate(serverActiveExecPlans);
            this.activeExecPlans = serverActiveExecPlans;

            let serverFinalizedExecPlans = manualExecPlans.finalizedExecPlans;
            this.sortByFinalizedDate(serverFinalizedExecPlans);
            this.finalizedExecPlans = serverFinalizedExecPlans;
        });
    }

    sortByCreateDate(execPlans: ManualExecPlan[]) {
        execPlans.sort((left: ManualExecPlan, right: ManualExecPlan) => {

            let leftNodeText = left.createdDate.toISOString();
            let rightNodeText = right.createdDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

    sortByFinalizedDate(execPlans: ManualExecPlan[]) {
        execPlans.sort((left: ManualExecPlan, right: ManualExecPlan) => {

            let leftNodeText = left.finalizedDate.toISOString();
            let rightNodeText = right.finalizedDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

    ngOnDestroy(): void {
        if(this.getExecPlansSubscription) this.getExecPlansSubscription.unsubscribe();
    }

    onCreateNewTestPlan() {
        this.urlService.navigateToManualExecPlanCreate()
    }
}
