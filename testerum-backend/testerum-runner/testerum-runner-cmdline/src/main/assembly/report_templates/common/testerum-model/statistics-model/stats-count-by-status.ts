import {ExecutionStatus} from "../report-model/model/report/execution-status";
import {MarshallingUtils} from "../json-marshalling/marshalling-utils";

export class StatsCountByStatus {

    constructor(public readonly countByStatusMap: Map<ExecutionStatus, number>) {}

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
