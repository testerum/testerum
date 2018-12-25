import {Component} from '@angular/core';
import {ReportUrlService} from "../service/report-url.service";

@Component({
    selector: 'app-report',
    templateUrl: './report.component.html',
    styleUrls: ['./report.component.scss']
})
export class ReportComponent {

    constructor(private reportUrlService: ReportUrlService){}

    goToTagsOverview () {
        this.reportUrlService.navigateToTagsOverview();
    }
}
