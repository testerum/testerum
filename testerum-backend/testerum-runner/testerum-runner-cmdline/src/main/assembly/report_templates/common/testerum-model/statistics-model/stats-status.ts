import {StatsCountByStatus} from "./stats-count-by-status";
import {MarshallingUtils} from "../json-marshalling/marshalling-utils";

export class StatsStatus {

    constructor(public readonly suiteCount: StatsCountByStatus,
                public readonly testAvg: StatsCountByStatus,
                public readonly perTagAvg: Map<string, StatsCountByStatus>) { }

    static parse(input: Object): StatsStatus {
        if (!input) {
            return null;
        }

        const suiteCount = StatsCountByStatus.parse(input["suiteCount"]);
        const testAvg = StatsCountByStatus.parse(input["testAvg"]);
        const perTagAvg = MarshallingUtils.parseMap(
            input["perTagAvg"],
            (key) => key as string,
            (value) => StatsCountByStatus.parse(value)
        );

        return new StatsStatus(suiteCount, testAvg, perTagAvg);
    }

}
