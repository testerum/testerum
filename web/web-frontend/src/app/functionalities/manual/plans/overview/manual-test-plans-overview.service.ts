import {Injectable} from '@angular/core';
import {ManualTestPlan} from "../model/manual-test-plan.model";
import {Subscription} from "rxjs";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {ManualTestPlans} from "../model/manual-test-plans.model";

@Injectable()
export class ManualTestPlansOverviewService {

    activeTestPlans: Array<ManualTestPlan> = [];
    finalizedTestPlans: Array<ManualTestPlan> = [];

    private getExecPlansSubscription: Subscription;

    constructor(private manualExecPlansService: ManualTestPlansService) {
    }

    initializeManualPlansOverview() {
        if(this.getExecPlansSubscription) this.getExecPlansSubscription.unsubscribe();

        this.getExecPlansSubscription = this.manualExecPlansService.getExecPlans().subscribe((manualExecPlans: ManualTestPlans) => {
            let serverActiveTestPlans = manualExecPlans.activeTestPlans;
            this.sortByCreateDate(serverActiveTestPlans);
            this.activeTestPlans = serverActiveTestPlans;

            let serverFinalizedTestPlans = manualExecPlans.finalizedTestPlans;
            this.sortByFinalizedDate(serverFinalizedTestPlans);
            this.finalizedTestPlans = serverFinalizedTestPlans;
        });
    }

    private sortByCreateDate(execPlans: ManualTestPlan[]) {
        execPlans.sort((left: ManualTestPlan, right: ManualTestPlan) => {

            let leftNodeText = left.createdDate.toISOString();
            let rightNodeText = right.createdDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

    private sortByFinalizedDate(execPlans: ManualTestPlan[]) {
        execPlans.sort((left: ManualTestPlan, right: ManualTestPlan) => {

            let leftNodeText = left.finalizedDate.toISOString();
            let rightNodeText = right.finalizedDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

}
