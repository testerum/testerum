import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {StatsService} from "../../service/stats.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPoint} from "../../model/DataPoint";
import {UIChart} from "primeng/chart";

@Component({
    selector: 'tests-stats',
    templateUrl: './tests-stats.component.html',
    styleUrls: ['./tests-stats.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class TestsStatsComponent implements OnInit {

    data: any;
    options: any;

    firstPossibleDate: Date;
    lastPossibleDate: Date;

    firstDisplayedDate: Date;
    lastDisplayedDate: Date;

    rangeDates: Date[] = [];

    allPassedTestData: DataPoint[] = [];
    allFailedTestData: DataPoint[] = [];
    allUndefinedTestData: DataPoint[] = [];
    allDisabledTestData: DataPoint[] = [];
    allSkippedTestData: DataPoint[] = [];

    passedTestData: DataPoint[] = [];
    failedTestData: DataPoint[] = [];
    undefinedTestData: DataPoint[] = [];
    disabledTestData: DataPoint[] = [];
    skippedTestData: DataPoint[] = [];

    @ViewChild("chart") chart: UIChart;

    constructor(private statsService: StatsService) {
    }

    ngOnInit() {
        this.allPassedTestData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.PASSED, null, null);
        this.allFailedTestData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.FAILED, null, null);
        this.allUndefinedTestData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.UNDEFINED, null, null);
        this.allDisabledTestData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.DISABLED, null, null);
        this.allSkippedTestData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.SKIPPED, null, null);

        this.passedTestData = this.allPassedTestData;
        this.failedTestData = this.allFailedTestData;
        this.undefinedTestData = this.allUndefinedTestData;
        this.disabledTestData = this.allDisabledTestData;
        this.skippedTestData = this.allSkippedTestData;

        if (this.passedTestData.length > 0) {
            this.firstPossibleDate = this.passedTestData[0].x;
            this.lastPossibleDate = this.passedTestData[this.passedTestData.length - 1].x;

            this.firstDisplayedDate = this.firstPossibleDate;
            this.lastDisplayedDate = this.lastPossibleDate;

            this.rangeDates.push(this.firstDisplayedDate);
            this.rangeDates.push(this.lastDisplayedDate);
        }

        this.data = this.createData();

        this.options = {
            legend: {
                position: 'bottom'
            },
            scales: {
                xAxes: [{
                    type: 'time'
                }],
                yAxes: [{
                    stacked: true
                }]
            }
        };
    }

    getMinYear() {
        return this.firstPossibleDate ? ""+this.firstPossibleDate.getFullYear() : "";
    }

    getMaxYear(): string {
        return this.lastPossibleDate ? ""+this.lastPossibleDate.getFullYear() : "";
    }

    onDateRangeChange(event: Array<Date>) {
        if (event[0]) {
            this.firstDisplayedDate = event[0];
        } else {
            this.firstDisplayedDate = this.firstPossibleDate;
        }
        if (event[1]) {
            this.lastDisplayedDate = event[1];
        } else {
            this.lastDisplayedDate = this.lastPossibleDate;
        }

        this.refreshDisplayedData();
    }

    private refreshDisplayedData() {
        this.passedTestData = [];
        for (const dataPoint of this.allPassedTestData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.passedTestData.push(dataPoint)
            }
        }
        this.failedTestData = [];
        for (const dataPoint of this.allFailedTestData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.failedTestData.push(dataPoint)
            }
        }
        this.undefinedTestData = [];
        for (const dataPoint of this.allUndefinedTestData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.undefinedTestData.push(dataPoint)
            }
        }
        this.disabledTestData = [];
        for (const dataPoint of this.allDisabledTestData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.disabledTestData.push(dataPoint)
            }
        }
        this.skippedTestData = [];
        for (const dataPoint of this.allSkippedTestData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.skippedTestData.push(dataPoint)
            }
        }
        this.data = this.createData();
    }

    private createData(): any {
        return {
            datasets: [

                {
                    label: 'Disabled',
                    data: this.disabledTestData,
                    fill: true,
                    borderColor: "#c0bebc",
                    backgroundColor: "#c0bebc"
                },
                {
                    label: 'Skipped',
                    data: this.skippedTestData,
                    fill: true,
                    borderColor: "#aae8ff",
                    backgroundColor: "#aae8ff"
                },
                {
                    label: 'Undefined',
                    data: this.undefinedTestData,
                    fill: true,
                    borderColor: "#FFCE56",
                    backgroundColor: "#ffce56"
                },
                {
                    label: 'Passed',
                    data: this.passedTestData,
                    fill: true,
                    borderColor: "#5cb85c",
                    backgroundColor: "#5cb85c"
                },
                {
                    label: 'Failed',
                    data: this.failedTestData,
                    fill: true,
                    borderColor: "#FF6384",
                    backgroundColor: "#ff6384"
                }
            ]
        };
    }
}
