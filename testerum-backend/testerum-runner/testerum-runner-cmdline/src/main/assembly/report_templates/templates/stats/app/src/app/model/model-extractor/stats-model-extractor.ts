import {Stats} from "../../../../../../../common/testerum-model/statistics-model/model/stats";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPointModel} from "../data-point.model";
import {StatsAll} from "../../../../../../../common/testerum-model/statistics-model/model/stats-all";
import {StatsCountByStatus} from "../../../../../../../common/testerum-model/statistics-model/model/stats-count-by-status";
import {ArrayUtil} from "../../../../../../pretty/app/src/app/util/array.util";

export class StatsModelExtractor {

    readonly stats: Stats;

    constructor(reportSuite: Stats) {
        this.stats = reportSuite;
    }

    getTestData(status: ExecutionStatus): Array<DataPointModel> {
        let result: Array<DataPointModel> = [];

        if (!this.stats || !this.stats.perDay) {
            return result;
        }

        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            let amount = value.status.testAvg.getCount(status);
            amount = amount? amount: 0;
            let dataPoint = new DataPointModel(key, amount);
            result.push(dataPoint);
        });

        return result;
    }

    getSuitesData(status: ExecutionStatus): Array<DataPointModel> {
        let result: Array<DataPointModel> = [];

        if (!this.stats || !this.stats.perDay) {
            return result;
        }

        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            let amount = value.status.suiteCount.getCount(status);
            amount = amount? amount: 0;
            let dataPoint = new DataPointModel(key, amount);
            result.push(dataPoint);
        });

        return result;
    }

    getTagData(status: ExecutionStatus, tag: string): Array<DataPointModel> {
        let result: Array<DataPointModel> = [];

        if (!this.stats || !this.stats.perDay) {
            return result;
        }

        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            let tagStats = value.status.perTagAvg.get(tag);
            let amount = tagStats.getCount(status);
            amount = amount? amount: 0;
            let dataPoint = new DataPointModel(key, amount);
            result.push(dataPoint);
        });

        return result;
    }

    getTags(): Array<string> {
        let result: Array<string> = [];

        if (!this.stats || !this.stats.perDay) {
            return result;
        }

        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            if(!value.status.perTagAvg) {return}
            value.status.perTagAvg.forEach((value1: StatsCountByStatus, key1: string) => {
                if (ArrayUtil.containsElement(result, key1)) {return}
                result.push(key1);
            });
        });

        return result;
    }

    getFirstDate(): Date {

        if (!this.stats || !this.stats.perDay) {
            return null;
        }

        return this.stats.perDay.keys().next().value;
    }

    getLastDate(): Date {

        if (!this.stats || !this.stats.perDay) {
            return null;
        }

        let lastDate: Date = null;
        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            lastDate = key;
        });

        return lastDate;
    }
}
