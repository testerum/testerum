import {Stats} from "../../../../../../../common/testerum-model/statistics-model/model/stats";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {DataPoint} from "../DataPoint";
import {StatsAll} from "../../../../../../../common/testerum-model/statistics-model/model/stats-all";

export class StatsModelExtractor {

    readonly stats: Stats;

    constructor(reportSuite: Stats) {
        this.stats = reportSuite;
    }

    getTestData(status: ExecutionStatus, startDate: Date, endDate: Date): Array<DataPoint> {
        let result: Array<DataPoint> = [];

        if (!this.stats || !this.stats.perDay) {
            return result;
        }

        this.stats.perDay.forEach((value: StatsAll, key: Date) => {
            if (startDate && key < startDate) {return;}
            if (endDate && endDate < key) {return;}

            let amount = value.status.testAvg.getCount(status);
            amount = amount? amount: 0;
            let dataPoint = new DataPoint(key, amount);
            result.push(dataPoint);
        });

        return result;
    }
}
