import {Component, OnDestroy, OnInit} from '@angular/core';
import {ManualTestPlan} from "../model/manual-test-plan.model";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {ManualTestPlans} from "../model/manual-test-plans.model";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../service/url.service";

@Component({
    selector: 'manual-test-plans-overview',
    templateUrl: 'manual-test-plans-overview.component.html',
    styleUrls: ['manual-test-plans-overview.component.scss']
})
export class ManualTestPlansOverviewComponent implements OnInit, OnDestroy {

    activeTestPlans: Array<ManualTestPlan> = [];
    finalizedTestPlans: Array<ManualTestPlan> = [];

    getExecPlansSubscription: Subscription;
    constructor(private manualExecPlansService: ManualTestPlansService,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.getExecPlansSubscription = this.manualExecPlansService.getExecPlans().subscribe((manualExecPlans: ManualTestPlans) => {
            let serverActiveTestPlans = manualExecPlans.activeTestPlans;
            this.sortByCreateDate(serverActiveTestPlans);
            this.activeTestPlans = serverActiveTestPlans;

            let serverFinalizedTestPlans = manualExecPlans.finalizedTestPlans;
            this.sortByFinalizedDate(serverFinalizedTestPlans);
            this.finalizedTestPlans = serverFinalizedTestPlans;
        });
    }

    sortByCreateDate(execPlans: ManualTestPlan[]) {
        execPlans.sort((left: ManualTestPlan, right: ManualTestPlan) => {

            let leftNodeText = left.createdDate.toISOString();
            let rightNodeText = right.createdDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

    sortByFinalizedDate(execPlans: ManualTestPlan[]) {
        execPlans.sort((left: ManualTestPlan, right: ManualTestPlan) => {

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
