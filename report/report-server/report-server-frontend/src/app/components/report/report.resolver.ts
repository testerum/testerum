import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {ReportService} from "../../service/report.service";
import {ReportInfo} from "../../model/report-info.model";
import {SelectedReport} from "./model/selected-report.model";
import {map} from "rxjs/operators";

@Injectable()
export class ReportResolver implements Resolve<any> {

  constructor(private route: ActivatedRoute,
              private reportService: ReportService) {
  }

  resolve(route: ActivatedRouteSnapshot) {

    let reportType = route.params["reportType"];
    let projectId = route.params["projectId"];

    return this.reportService.getReportsInfo().pipe(
      map(reports => {
        let selectedReportInfo =   ReportResolver.findReport(reports, projectId)
        return new SelectedReport(reportType, selectedReportInfo)
      }
    ));
  }

  private static findReport(reports: Array<ReportInfo>, projectId: string): ReportInfo {
    for (const report of reports) {
      if (report.projectId == projectId) {
        return report
      }
    }

    return null;
  }
}
