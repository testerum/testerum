import {Component, OnInit} from '@angular/core';
import {ManualTestsOverviewService} from "./manual-tests-overview.service";

@Component({
    selector: 'manual-tests-overview',
    templateUrl: 'manual-tests-overview.component.html',
    styleUrls: ['manual-tests-overview.component.scss', '../../../generic/css/forms.scss']
})

export class ManualTestsOverviewComponent implements OnInit {

    manualTestsOverviewService: ManualTestsOverviewService;

    constructor(manualTestsOverviewService: ManualTestsOverviewService) {
        this.manualTestsOverviewService = manualTestsOverviewService;
    }

    ngOnInit() {
        this.manualTestsOverviewService.initializeRunnersOverview();
    }
}
