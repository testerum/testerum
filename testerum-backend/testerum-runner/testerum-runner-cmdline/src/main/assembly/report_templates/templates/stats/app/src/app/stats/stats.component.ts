import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {StatsService} from "../service/stats.service";
import {StatsType} from "../model/stats-type.enum";

@Component({
    selector: 'app-stats',
    templateUrl: './stats.component.html',
    styleUrls: ['./stats.component.scss']
})
export class StatsComponent implements OnInit{

    StatsType = StatsType;

    firstAbsoluteDate: Date;
    lastAbsoluteDate: Date;
    startDate: Date;
    endDate: Date;
    showDetails: boolean = false;

    tags: string[] = [];

    constructor(private statsService: StatsService) {
    }

    ngOnInit(): void {
        this.firstAbsoluteDate = this.statsService.statsModelExtractor.getFirstDate();
        this.lastAbsoluteDate = this.statsService.statsModelExtractor.getLastDate();
        this.startDate = this.firstAbsoluteDate;
        this.endDate = this.lastAbsoluteDate;

        this.tags = this.statsService.statsModelExtractor.getTags();
    }

    getTags(): string[] {
        return this.tags;
    }

    hasTags(): boolean {
        return this.tags && this.tags.length > 0;
    }

    getYearRange(): string {
        return this.firstAbsoluteDate.getFullYear() + ":" + this.lastAbsoluteDate.getFullYear();
    }

    onEndDateSelect(selectedDate: Date) {
        this.endDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate(), 23, 59, 59);
    }
}
