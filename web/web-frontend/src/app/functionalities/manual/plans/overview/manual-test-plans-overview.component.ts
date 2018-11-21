import {Component, OnInit} from '@angular/core';
import {UrlService} from "../../../../service/url.service";
import {ManualTestPlansOverviewService} from "./manual-test-plans-overview.service";

@Component({
    selector: 'manual-test-plans-overview',
    templateUrl: 'manual-test-plans-overview.component.html',
    styleUrls: ['manual-test-plans-overview.component.scss']
})
export class ManualTestPlansOverviewComponent implements OnInit {

    constructor(public manualTestPlansOverviewService: ManualTestPlansOverviewService,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.manualTestPlansOverviewService.initializeManualPlansOverview();
    }

    onCreateNewTestPlan() {
        this.urlService.navigateToManualExecPlanCreate()
    }
}
