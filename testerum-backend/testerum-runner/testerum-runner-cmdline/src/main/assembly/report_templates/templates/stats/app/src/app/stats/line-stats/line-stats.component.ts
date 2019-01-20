import {Component, ElementRef, HostBinding, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {StatsService} from "../../service/stats.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPointModel} from "../../model/data-point.model";
import {StatsType} from "../../model/stats-type.enum";
import {ArrayUtil} from "../../../../../../pretty/app/src/app/util/array.util";
import {UIChart} from "primeng/chart";

@Component({
    selector: 'line-stats',
    templateUrl: './line-stats.component.html',
    styleUrls: ['./line-stats.component.scss'],
})
export class LineStatsComponent implements OnInit, OnChanges {

    @Input() title: string;
    @Input() statsType: StatsType;
    @Input() tag: string;
    @Input()  widthInPercentage: number = 45;

    @Input() startDate: Date;
    @Input() endDate: Date;

    @Input() showDetails: boolean;

    data: any;
    options: any;

    firstPossibleDate: Date;
    lastPossibleDate: Date;

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

        this.firstPossibleDate = this.statsService.statsModelExtractor.getFirstDate();
        this.lastPossibleDate = this.statsService.statsModelExtractor.getLastDate();

        this.refreshDisplayedData();

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
            },
            animation: {
                duration: 0
            }
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['startDate'] || changes['endDate'] || changes['showDetails']) {
            this.refreshDisplayedData();
        }
    }

    private refreshDisplayedData() {
        this.passedData = [];
        for (const dataPoint of this.allPassedData) {
            if (this.startDate <= dataPoint.x && dataPoint.x <= this.endDate) {
                this.passedData.push(dataPoint)
            }
        }
        this.failedData = [];
        for (const dataPoint of this.allFailedData) {
            if (this.startDate <= dataPoint.x && dataPoint.x <= this.endDate) {
                this.failedData.push(dataPoint)
            }
        }
        this.undefinedData = [];
        for (const dataPoint of this.allUndefinedData) {
            if (this.startDate <= dataPoint.x && dataPoint.x <= this.endDate) {
                this.showDetails ? this.undefinedData.push(dataPoint) : this.addPointToFailedData(dataPoint);
            }
        }
        this.disabledData = [];
        for (const dataPoint of this.allDisabledData) {
            if (this.startDate <= dataPoint.x && dataPoint.x <= this.endDate) {
                this.showDetails ? this.disabledData.push(dataPoint) : this.addPointToFailedData(dataPoint);

            }
        }
        this.skippedData = [];
        for (const dataPoint of this.allSkippedData) {
            if (this.startDate <= dataPoint.x && dataPoint.x <= this.endDate) {
                this.showDetails ? this.skippedData.push(dataPoint) : this.addPointToFailedData(dataPoint);

            }
        }
        this.data = this.createData();
    }

    private createData(): any {
        let datasets = [];

        if (this.showDetails && this.dataFieldHasValues(this.disabledData)) {
            datasets.push(
                {
                    label: 'Disabled',
                    data: this.disabledData,
                    fill: true,
                    borderColor: "#c0bebc",
                    backgroundColor: "#c0bebc"
                }
            )
        }
        if (this.showDetails && this.dataFieldHasValues(this.skippedData)) {
            datasets.push(
                {
                    label: 'Skipped',
                    data: this.skippedData,
                    fill: true,
                    borderColor: "#aae8ff",
                    backgroundColor: "#aae8ff"
                }
            )
        }
        if (this.showDetails && this.dataFieldHasValues(this.undefinedData)) {
            datasets.push(
                {
                    label: 'Undefined',
                    data: this.undefinedData,
                    fill: true,
                    borderColor: "#FFCE56",
                    backgroundColor: "#ffce56"
                }
            )
        }
        if (this.dataFieldHasValues(this.passedData)) {
            datasets.push(
                {
                    label: 'Passed',
                    data: this.passedData,
                    fill: true,
                    borderColor: "#5cb85c",
                    backgroundColor: "#5cb85c"
                }
            );
        }
        if (this.dataFieldHasValues(this.failedData)) {
            datasets.push(
                {
                    label: 'Failed',
                    data: this.failedData,
                    fill: true,
                    borderColor: "#FF6384",
                    backgroundColor: "#ff6384"
                }
            );
        }

        return {datasets: datasets};
    }

    dataFieldHasValues(dataPoints: DataPointModel[]): boolean {
        for (const point of dataPoints) {
            if (point.y > 0) {
                return true
            }
        }
    }

    onMaxMinClick() {
        this.fullWidth = !this.fullWidth;
    }

    private addPointToFailedData(dataPoint: DataPointModel) {
        let failedDataPointIndex = this.failedData.findIndex(value => {return dataPoint.x == value.x});
        let failedDataPoint = this.failedData[failedDataPointIndex];
        this.failedData[failedDataPointIndex] = new DataPointModel(failedDataPoint.x, failedDataPoint.y + dataPoint.y)
    }
}
