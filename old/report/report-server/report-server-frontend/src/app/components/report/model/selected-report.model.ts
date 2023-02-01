import {ReportInfo} from "../../../model/report-info.model";

export class SelectedReport {
  reportType: string;
  reportInfo: ReportInfo;

  constructor(reportType: string, reportInfo: ReportInfo) {
    this.reportType = reportType;
    this.reportInfo = reportInfo;
  }
}
