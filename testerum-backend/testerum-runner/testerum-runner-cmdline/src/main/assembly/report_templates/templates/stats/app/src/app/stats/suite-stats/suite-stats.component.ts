import {Component, HostBinding, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {StatsService} from "../../service/stats.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPoint} from "../../model/DataPoint";
import {UIChart} from "primeng/chart";

@Component({
    selector: 'suite-stats',
    templateUrl: './suite-stats.component.html',
    styleUrls: ['./suite-stats.component.scss']
})
export class SuiteStatsComponent implements OnInit {

    data: any;
    options: any;

    firstPossibleDate: Date;
    lastPossibleDate: Date;

    firstDisplayedDate: Date;
    lastDisplayedDate: Date;

    rangeDates: Date[] = [];

    allPassedSuiteData: DataPoint[] = [];
    allFailedSuiteData: DataPoint[] = [];
    allUndefinedSuiteData: DataPoint[] = [];
    allDisabledSuiteData: DataPoint[] = [];
    allSkippedSuiteData: DataPoint[] = [];

    passedSuiteData: DataPoint[] = [];
    failedSuiteData: DataPoint[] = [];
    undefinedSuiteData: DataPoint[] = [];
    disabledSuiteData: DataPoint[] = [];
    skippedSuiteData: DataPoint[] = [];

    @HostBinding('class.full-width') fullWidth: boolean = false;
    @ViewChild("chart") chart: UIChart;

    constructor(private statsService: StatsService) {
    }

    ngOnInit() {
        this.allPassedSuiteData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.PASSED, null, null);
        this.allFailedSuiteData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.FAILED, null, null);
        this.allUndefinedSuiteData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.UNDEFINED, null, null);
        this.allDisabledSuiteData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.DISABLED, null, null);
        this.allSkippedSuiteData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.SKIPPED, null, null);

        this.passedSuiteData = this.allPassedSuiteData;
        this.failedSuiteData = this.allFailedSuiteData;
        this.undefinedSuiteData = this.allUndefinedSuiteData;
        this.disabledSuiteData = this.allDisabledSuiteData;
        this.skippedSuiteData = this.allSkippedSuiteData;

        if (this.passedSuiteData.length > 0) {
            this.firstPossibleDate = this.passedSuiteData[0].x;
            this.lastPossibleDate = this.passedSuiteData[this.passedSuiteData.length - 1].x;

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
        this.passedSuiteData = [];
        for (const dataPoint of this.allPassedSuiteData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.passedSuiteData.push(dataPoint)
            }
        }
        this.failedSuiteData = [];
        for (const dataPoint of this.allFailedSuiteData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.failedSuiteData.push(dataPoint)
            }
        }
        this.undefinedSuiteData = [];
        for (const dataPoint of this.allUndefinedSuiteData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.undefinedSuiteData.push(dataPoint)
            }
        }
        this.disabledSuiteData = [];
        for (const dataPoint of this.allDisabledSuiteData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.disabledSuiteData.push(dataPoint)
            }
        }
        this.skippedSuiteData = [];
        for (const dataPoint of this.allSkippedSuiteData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.skippedSuiteData.push(dataPoint)
            }
        }
        this.data = this.createData();
    }

    private createData(): any {
        return {
            datasets: [

                {
                    label: 'Disabled',
                    data: this.disabledSuiteData,
                    fill: true,
                    borderColor: "#c0bebc",
                    backgroundColor: "#c0bebc"
                },
                {
                    label: 'Skipped',
                    data: this.skippedSuiteData,
                    fill: true,
                    borderColor: "#aae8ff",
                    backgroundColor: "#aae8ff"
                },
                {
                    label: 'Undefined',
                    data: this.undefinedSuiteData,
                    fill: true,
                    borderColor: "#FFCE56",
                    backgroundColor: "#ffce56"
                },
                {
                    label: 'Passed',
                    data: this.passedSuiteData,
                    fill: true,
                    borderColor: "#5cb85c",
                    backgroundColor: "#5cb85c"
                },
                {
                    label: 'Failed',
                    data: this.failedSuiteData,
                    fill: true,
                    borderColor: "#FF6384",
                    backgroundColor: "#ff6384"
                }
            ]
        };
    }

    onMaxMinClick() {
        this.fullWidth = !this.fullWidth;
    }
}
