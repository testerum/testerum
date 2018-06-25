import {Component, OnInit} from '@angular/core';
import {ManualTestsRunnerService} from "../service/manual-tests-runner.service";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {ManualTestsRunnerStatus} from "../model/enums/manual-tests-runner-status.enum";
import {ManualTestsOverviewService} from "./manual-tests-overview.service";

@Component({
    selector: 'manual-tests-overview',
    templateUrl: 'manual-tests-overview.component.html',
    styleUrls: ['manual-tests-overview.component.css', '../../../generic/css/forms.css']
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
