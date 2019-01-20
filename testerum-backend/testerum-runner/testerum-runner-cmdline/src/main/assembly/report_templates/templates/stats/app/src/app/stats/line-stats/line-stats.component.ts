import {Component, ElementRef, HostBinding, Input, OnInit, ViewChild} from '@angular/core';
import {StatsService} from "../../service/stats.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPointModel} from "../../model/data-point.model";
import {UIChart} from "primeng/chart";
import {StatsType} from "../../model/stats-type.enum";

@Component({
    selector: 'line-stats',
    templateUrl: './line-stats.component.html',
    styleUrls: ['./line-stats.component.scss'],
})
export class LineStatsComponent implements OnInit {

    @Input() title: string;
    @Input() statsType: StatsType;
    @Input() tag: string;
    @Input()  widthInPercentage: number = 45;

    data: any;
    options: any;

    firstPossibleDate: Date;
    lastPossibleDate: Date;

    firstDisplayedDate: Date;
    lastDisplayedDate: Date;

    rangeDates: Date[] = [];

    allPassedData: DataPointModel[] = [];
    allFailedData: DataPointModel[] = [];
    allUndefinedData: DataPointModel[] = [];
    allDisabledData: DataPointModel[] = [];
    allSkippedData: DataPointModel[] = [];

    passedData: DataPointModel[] = [];
    failedData: DataPointModel[] = [];
    undefinedData: DataPointModel[] = [];
    disabledData: DataPointModel[] = [];
    skippedData: DataPointModel[] = [];

    @HostBinding('class.full-width') fullWidth: boolean = false;

    @ViewChild("chart") chart: UIChart;

    constructor(private element: ElementRef,
                private statsService: StatsService) {
    }

    ngOnInit() {
        let elem = this.element.nativeElement;
        elem.style.width = this.widthInPercentage+"%";

        if (this.statsType == StatsType.TESTS_RESULTS) {
            this.allPassedData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.PASSED);
            this.allFailedData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.FAILED);
            this.allUndefinedData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.UNDEFINED);
            this.allDisabledData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.DISABLED);
            this.allSkippedData = this.statsService.statsModelExtractor.getTestData(ExecutionStatus.SKIPPED);
        }

        if (this.statsType == StatsType.SUITES_RESULTS) {
            this.allPassedData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.PASSED);
            this.allFailedData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.FAILED);
            this.allUndefinedData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.UNDEFINED);
            this.allDisabledData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.DISABLED);
            this.allSkippedData = this.statsService.statsModelExtractor.getSuitesData(ExecutionStatus.SKIPPED);
        }

        if (this.statsType == StatsType.TAG_RESULTS) {
            this.allPassedData = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.PASSED, this.tag);
            this.allFailedData = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.FAILED, this.tag);
            this.allUndefinedData = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.UNDEFINED, this.tag);
            this.allDisabledData = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.DISABLED, this.tag);
            this.allSkippedData = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.SKIPPED, this.tag);
        }

        this.passedData = this.allPassedData;
        this.failedData = this.allFailedData;
        this.undefinedData = this.allUndefinedData;
        this.disabledData = this.allDisabledData;
        this.skippedData = this.allSkippedData;

        if (this.passedData.length > 0) {
            this.firstPossibleDate = this.passedData[0].x;
            this.lastPossibleDate = this.passedData[this.passedData.length - 1].x;

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
        this.passedData = [];
        for (const dataPoint of this.allPassedData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.passedData.push(dataPoint)
            }
        }
        this.failedData = [];
        for (const dataPoint of this.allFailedData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.failedData.push(dataPoint)
            }
        }
        this.undefinedData = [];
        for (const dataPoint of this.allUndefinedData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.undefinedData.push(dataPoint)
            }
        }
        this.disabledData = [];
        for (const dataPoint of this.allDisabledData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.disabledData.push(dataPoint)
            }
        }
        this.skippedData = [];
        for (const dataPoint of this.allSkippedData) {
            if (this.firstDisplayedDate <= dataPoint.x && dataPoint.x <= this.lastDisplayedDate) {
                this.skippedData.push(dataPoint)
            }
        }
        this.data = this.createData();
    }

    private createData(): any {
        return {
            datasets: [

                {
                    label: 'Disabled',
                    data: this.disabledData,
                    fill: true,
                    borderColor: "#c0bebc",
                    backgroundColor: "#c0bebc"
                },
                {
                    label: 'Skipped',
                    data: this.skippedData,
                    fill: true,
                    borderColor: "#aae8ff",
                    backgroundColor: "#aae8ff"
                },
                {
                    label: 'Undefined',
                    data: this.undefinedData,
                    fill: true,
                    borderColor: "#FFCE56",
                    backgroundColor: "#ffce56"
                },
                {
                    label: 'Passed',
                    data: this.passedData,
                    fill: true,
                    borderColor: "#5cb85c",
                    backgroundColor: "#5cb85c"
                },
                {
                    label: 'Failed',
                    data: this.failedData,
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
