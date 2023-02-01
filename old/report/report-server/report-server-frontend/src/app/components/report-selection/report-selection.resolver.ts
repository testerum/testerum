import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {ReportService} from "../../service/report.service";

@Injectable()
export class ReportSelectionResolver implements Resolve<any> {

  constructor(private route: ActivatedRoute,
              private reportService: ReportService) {
  }

  resolve(route: ActivatedRouteSnapshot) {
    return this.reportService.getReportsInfo();
  }
}
