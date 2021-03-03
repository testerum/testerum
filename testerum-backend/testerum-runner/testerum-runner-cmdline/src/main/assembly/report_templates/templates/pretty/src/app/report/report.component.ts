import {Component, OnInit} from '@angular/core';
import {ReportUrlService} from "../service/report-url.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-report',
    templateUrl: './report.component.html',
    styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {

    hideLinksToOtherReports = false;

    constructor(private activatedRoute: ActivatedRoute,
                private reportUrlService: ReportUrlService){}

    ngOnInit(): void {
        this.hideLinksToOtherReports = (this.activatedRoute.snapshot.queryParams["hideLinksToOtherReports"] == "true");
    }

    goToTagsOverview () {
        this.reportUrlService.navigateToTagsOverview();
    }
}
