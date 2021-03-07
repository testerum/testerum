import {Component, Input, OnInit} from '@angular/core';
import {ReportPieModel} from "./model/report-pie.model";
import {ExecutionStatus} from "../../../../../../common/testerum-model/report-model/model/report/execution-status";

@Component({
    selector: 'report-tag-pie',
    templateUrl: './report-tag-pie.component.html',
    styleUrls: ['./report-tag-pie.component.scss']
})
export class ReportTagPieComponent implements OnInit {

    @Input() statusByTagMap: Map<string, ExecutionStatus>;

    pieChartData: any;
    pieChartOptions:any;

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

        model.totalTests = this.statusByTagMap.size;
        this.statusByTagMap.forEach((value: ExecutionStatus, key: string) => {
            switch (value) {
                case ExecutionStatus.PASSED: {model.passed ++; break;}
                case ExecutionStatus.FAILED: {model.failed ++; break;}
                case ExecutionStatus.DISABLED: {model.disabled ++; break;}
                case ExecutionStatus.UNDEFINED: {model.undefined ++; break;}
                case ExecutionStatus.SKIPPED: {model.skipped ++; break;}
            }
        });
        return model;
    }
}
