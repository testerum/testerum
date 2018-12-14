import {RunnerReportNode, RunnerReportNodeType} from "./runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {ReportLog} from "./report-log";
import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";
import {ReportTest} from "./report-test";
import {ReportFeature} from "./report-feature";

export class ReportSuite implements RunnerReportNode {

    constructor(public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly logs: Array<ReportLog>,
                public readonly children: Array<FeatureOrTestRunnerReportNode>) { }

    static parse(input: Object): ReportSuite {
        if (!input) {
            return null;
        }

        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const logs = MarshallingUtils.parseList(input["logs"], ReportLog);
        const children = MarshallingUtils.parseListPolymorphically<FeatureOrTestRunnerReportNode>(input["children"], {
            [RunnerReportNodeType[RunnerReportNodeType.FEATURE]]: ReportFeature,
            [RunnerReportNodeType[RunnerReportNodeType.TEST]]: ReportTest
        });

        return new ReportSuite(startTime, endTime, durationMillis, status, logs, children);
    }

}
