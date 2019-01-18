import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {RunnerReportNodeType} from "./runner-report-node";
import {ReportTest} from "./report-test";

export class ReportFeature implements FeatureOrTestRunnerReportNode {

    constructor(public readonly featureName: string,
                public readonly tags: Array<string>,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<FeatureOrTestRunnerReportNode>) {
    }

    static parse(input: Object): ReportFeature {
        if (!input) {
            return null;
        }

        const featureName = input["featureName"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];
        const children = MarshallingUtils.parseListPolymorphically<FeatureOrTestRunnerReportNode>(input["children"], {
            [RunnerReportNodeType[RunnerReportNodeType.FEATURE]]: ReportFeature,
            [RunnerReportNodeType[RunnerReportNodeType.TEST]]: ReportTest
        });

        return new ReportFeature(featureName, tags, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children);
    }

}
