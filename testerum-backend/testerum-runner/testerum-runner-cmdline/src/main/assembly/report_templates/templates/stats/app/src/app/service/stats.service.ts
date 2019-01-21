import {Injectable} from '@angular/core';
import {StatsModelExtractor} from "../model/model-extractor/stats-model-extractor";
import {Stats} from "../../../../../../common/testerum-model/statistics-model/model/stats";

@Injectable()
export class StatsService {

    readonly statsModelExtractor: StatsModelExtractor;

    constructor() {
        let statsModelUnParsed = window['testerumRunnerStatisticsModel'];
        let statsSuite = Stats.parse(statsModelUnParsed);
        this.statsModelExtractor = new StatsModelExtractor(statsSuite);
    }
}
