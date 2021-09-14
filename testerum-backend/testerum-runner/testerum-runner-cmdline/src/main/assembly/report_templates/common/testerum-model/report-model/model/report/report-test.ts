import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {ReportStep} from "./report-step";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {RunnerReportNode, RunnerReportNodeType} from "./runner-report-node";
import {ReportHooks} from "./report-hooks";

export class ReportTest implements FeatureOrTestRunnerReportNode {

    constructor(public readonly testName: string,
                public readonly testFilePath: string,
                public readonly tags: Array<string>,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<RunnerReportNode>) {}

    static parse(input: Object): ReportTest {
        if (!input) {
            return null;
        }

        const testName = input["testName"];
        const testFilePath = input["testFilePath"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const startTime = MarshallingUtils.parseUtcToLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseUtcToLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];
        const children = MarshallingUtils.parseListPolymorphically<RunnerReportNode>(input["children"], {
            [RunnerReportNodeType[RunnerReportNodeType.STEP]]: ReportStep,
            [RunnerReportNodeType[RunnerReportNodeType.REPORT_HOOKS]]: ReportHooks
        });

        return new ReportTest(testName, testFilePath, tags, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children);
    }
}
