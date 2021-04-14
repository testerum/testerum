import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {SelectedReport} from "./model/selected-report.model";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {Location} from "@angular/common";

@Component({
  selector: 'report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit, OnDestroy {


  selectedReport: SelectedReport
  reportUrl: SafeUrl

  private routeSubscription: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer,
              private location: Location) {
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.data.subscribe(data => {
      this.selectedReport = data['selectedReport'];

      let reportUrl = this.location.prepareExternalUrl("/static-reports");
      switch (this.selectedReport.reportType) {
        case "statistics": { reportUrl += this.selectedReport.reportInfo.statisticsReportPath; break; }
        case "dashboard": { reportUrl += this.selectedReport.reportInfo.autoRefreshDashboardPath; break; }
        case "latest-report": { reportUrl += this.selectedReport.reportInfo.latestReportPath; break; }
      }
      this.reportUrl = this.sanitizer.bypassSecurityTrustResourceUrl(reportUrl);
    });
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
  }
}
