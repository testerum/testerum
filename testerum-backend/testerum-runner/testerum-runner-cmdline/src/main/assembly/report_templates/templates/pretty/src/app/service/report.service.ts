import {Injectable} from '@angular/core';
import {ReportSuite} from "../../../../../common/testerum-model/report-model/model/report/report-suite";
import {ReportModelExtractor} from "../../../../../common/testerum-model/report-model/model-extractor/report-model-extractor";

@Injectable()
export class ReportService {

  readonly reportModelExtractor: ReportModelExtractor;

  constructor() {
    let reportModelUnParsed = window['testerumRunnerReportModel'];
    let reportSuite = ReportSuite.parse(reportModelUnParsed);
    this.reportModelExtractor = new ReportModelExtractor(reportSuite);
  }
}
