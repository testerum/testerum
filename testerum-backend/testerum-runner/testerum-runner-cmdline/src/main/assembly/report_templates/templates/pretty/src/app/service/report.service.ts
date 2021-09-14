import {Injectable} from '@angular/core';
import {ReportSuite} from "../../../../../common/testerum-model/report-model/model/report/report-suite";
import {ReportModelExtractor} from "../../../../../common/testerum-model/report-model/model-extractor/report-model-extractor";

@Injectable()
export class ReportService {

  readonly reportModelExtractor: ReportModelExtractor;

  constructor() {
    // use for DEBUGGING in development.
    // execute the Runner job with a brake point at the end of the class testerum\report\report-generators\src\main\kotlin\com\testerum\report_generators\reports\report_model\base\BaseReportModelExecutionListener.kt
    // Copy the JSON and paste in the next line.
    // let reportModelUnParsed = JSON.parse('{replace here with JSON}');

    let reportModelUnParsed = window['testerumRunnerReportModel'];
    let reportSuite = ReportSuite.parse(reportModelUnParsed);
    this.reportModelExtractor = new ReportModelExtractor(reportSuite);
  }
}
