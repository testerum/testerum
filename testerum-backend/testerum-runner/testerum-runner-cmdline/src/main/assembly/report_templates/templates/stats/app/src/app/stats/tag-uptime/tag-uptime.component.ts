import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {StatsService} from "../../service/stats.service";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPointModel} from "../../model/data-point.model";
import {TagUptime} from "./tag-uptime/tag-uptime.modal";
import {ArrayUtil} from "../../../../../../pretty/app/src/app/util/array.util";

@Component({
    selector: 'tag-uptime',
    templateUrl: './tag-uptime.component.html',
    styleUrls: ['./tag-uptime.component.scss'],
    encapsulation:ViewEncapsulation.None
})
export class TagUptimeComponent implements OnInit, OnChanges {

    @Input() startDate: Date;
    @Input() endDate: Date;
    @Input()  widthInPercentage: number = 45;

    tagsUptime: TagUptime[] = [];
    cols: any[];
    tags: string[] = [];

    constructor(private element: ElementRef,
                private statsService: StatsService) {
    }

    ngOnInit() {
        let elem = this.element.nativeElement;
        elem.style.width = this.widthInPercentage+"%";

        this.tags = this.statsService.statsModelExtractor.getTags();

        this.cols = [
            { field: 'tag', header: 'Tag Name' },
            { field: 'uptime', header: 'Uptime' }
        ];

        this.refreshDisplayedData()
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['startDate'] || changes['endDate'] || changes['showDetails']) {
            this.refreshDisplayedData();
        }
    }

    private refreshDisplayedData() {

        this.tagsUptime = [];
        for (const tag of this.tags) {
            let passedTagPoints = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.PASSED, tag);
            let failedTagPoints = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.FAILED, tag);
            let undefinedTagPoints = this.statsService.statsModelExtractor.getTagData(ExecutionStatus.UNDEFINED, tag);

            let passedSum = 0;
            passedTagPoints.forEach((point: DataPointModel) => {
                if (this.startDate <= point.x && point.x <= this.endDate) {
                    passedSum += point.y
                }
            });

            let failedSum = 0;
            failedTagPoints.forEach((point: DataPointModel) => {
                if (this.startDate <= point.x && point.x <= this.endDate) {
                    failedSum += point.y
                }
            });
            undefinedTagPoints.forEach((point: DataPointModel) => {
                if (this.startDate <= point.x && point.x <= this.endDate) {
                    failedSum += point.y
                }
            });

            let uptime = 0;
            if (passedSum != 0) {
                uptime = (passedSum / (passedSum + failedSum)) * 100;
            }

            this.tagsUptime.push(new TagUptime(tag, uptime));
        }
        this.tagsUptime.sort((a,b) => a.uptime > b.uptime ? -1 : 1);
    }
}
