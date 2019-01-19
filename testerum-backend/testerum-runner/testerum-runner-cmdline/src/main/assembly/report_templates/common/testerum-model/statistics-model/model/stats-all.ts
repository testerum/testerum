import {StatsStatus} from "./stats-status";
import {Avg} from "./avg";

export class StatsAll {

    constructor(public readonly status: StatsStatus,
                public readonly suiteAvgDurationMillis: Avg) { }

    static parse(input: Object): StatsAll {
        if (!input) {
            return null;
        }

        const status = StatsStatus.parse(input["status"]);
        const suiteAvgDurationMillis = Avg.parse(input["suiteAvgDurationMillis"]);

        return new StatsAll(status, suiteAvgDurationMillis);
    }

}
