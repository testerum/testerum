import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {ReportStep} from "./report-step";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {ReportScenario} from "./report-scenario";

export class ReportParametrizedTest implements FeatureOrTestRunnerReportNode {

    constructor(public readonly testName: string,
                public readonly testFilePath: string,
                public readonly tags: Array<string>,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<ReportScenario>) {}

    static parse(input: Object): ReportParametrizedTest {
        if (!input) {
            return null;
        }

        const testName = input["testName"];
        const testFilePath = input["testFilePath"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];
        const children = MarshallingUtils.parseList(input["children"], ReportScenario);

        return new ReportParametrizedTest(testName, testFilePath, tags, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children);
    }
}
