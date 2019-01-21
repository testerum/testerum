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

    constructor(private statsService: StatsService) {
    }

    ngOnInit(): void {
        this.firstAbsoluteDate = this.statsService.statsModelExtractor.getFirstDate();
        this.lastAbsoluteDate = this.statsService.statsModelExtractor.getLastDate();
        this.startDate = this.firstAbsoluteDate;
        this.endDate = this.lastAbsoluteDate;
    }

    getTags(): string[] {
        return this.statsService.statsModelExtractor.getTags();
    }
}
