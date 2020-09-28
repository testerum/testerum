import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {ReportInfo} from "../../model/report-info.model";
import {faTachometerAlt} from "@fortawesome/free-solid-svg-icons/faTachometerAlt";
import {faChartPie} from "@fortawesome/free-solid-svg-icons/faChartPie";
import {faTasks} from "@fortawesome/free-solid-svg-icons/faTasks";

@Component({
  selector: 'report-selection',
  templateUrl: './report-selection.component.html',
  styleUrls: ['./report-selection.component.scss']
})
export class ReportSelectionComponent implements OnInit, OnDestroy {

  faTachometerAlt = faTachometerAlt
  faChartPie = faChartPie
  faTasks = faTasks

  reports: Array<ReportInfo> = []

  private routeSubscription: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.data.subscribe(data => {
      this.reports = data['reports'];
    });
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
  }
}
