import {ExecutionStatus} from "../report-model/model/report/execution-status";
import {MarshallingUtils} from "../json-marshalling/marshalling-utils";

export class StatsCountByStatus {

    constructor(public readonly countByStatusMap: Map<ExecutionStatus, number>) {}

    /**
     * safe way to get the count for a particular status,
     * since if the count is zero, the map doesn't contain it at all
     */
    getCount(status: ExecutionStatus): number {
        const count = this.countByStatusMap.get(status);

        if (count) {
            return count;
        } else {
            return 0;
        }
    }

    static parse(input: Object): StatsCountByStatus {
        if (!input) {
            return null;
        }

        const countByStatusMap = MarshallingUtils.parseMap(
            input,
            (key) => MarshallingUtils.parseEnum(key, ExecutionStatus),
            (value) => value as number
        );

        return new StatsCountByStatus(countByStatusMap);
    }

}
