import {Component, OnInit} from '@angular/core';
import {ReportPieModel} from "./model/report-pie.model";
import {ReportService} from "../../service/report.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";

@Component({
    selector: 'report-pie',
    templateUrl: './report-pie.component.html',
    styleUrls: ['./report-pie.component.scss']
})
export class ReportPieComponent implements OnInit {

    pieChartData: any;

    pieChartOptions:any;

    constructor(private reportService: ReportService) { }

    ngOnInit(): void {
        this.pieChartOptions = {
            animation: false,
            legend: {
                display: false,
            }
        };

        this.pieChartData = this.getPieDataFromModel(this.model());
    }

    private getPieDataFromModel(model: ReportPieModel): any {
        let passedColor = "#5cb85c";
        let failedColor = "#E6302C";
        let disabledColor = "#FF7800";
        let undefinedColor = "#FFCE56";
        let skippedColor = "#aae8ff";

        return {
            labels: [
                'Passed',
                'Failed',
                'Disabled',
                'Undefined steps',
                'Skipped'
            ],
            datasets: [{
                data: [
                    model.passed,
                    model.failed,
                    model.disabled,
                    model.undefined,
                    model.skipped
                ],
                backgroundColor: [passedColor, failedColor, disabledColor, undefinedColor, skippedColor],
                hoverBackgroundColor: [passedColor, failedColor, disabledColor, undefinedColor, skippedColor]
            }]
        };
    }

    model(): ReportPieModel {
        let model = new ReportPieModel();
        let tests = this.reportService.reportModelExtractor.getTests();

        model.totalTests = tests.length;
        for (const test of tests) {
            this.addExecutionStatusToReportPie(test.status, model);
        }

        let parametrizedTests = this.reportService.reportModelExtractor.getParametrizedTests();
        for (const parametrizedTest of parametrizedTests) {
            let scenarios = parametrizedTest.children;
            model.totalTests += scenarios.length;
            for (const scenario of scenarios) {
                this.addExecutionStatusToReportPie(scenario.status, model);
            }
        }
        return model;
    }

    private addExecutionStatusToReportPie(status: ExecutionStatus, model) {
        switch (status) {
            case ExecutionStatus.PASSED: { model.passed++; break; }
            case ExecutionStatus.FAILED: { model.failed++; break; }
            case ExecutionStatus.DISABLED: { model.disabled++; break; }
            case ExecutionStatus.UNDEFINED: { model.undefined++; break; }
            case ExecutionStatus.SKIPPED: { model.skipped++; break; }
        }
    }
}
