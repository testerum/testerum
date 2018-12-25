import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {ExceptionDetail} from "../exception/exception-detail";
import {ReportStep} from "./report-step";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";

export class ReportTest implements FeatureOrTestRunnerReportNode {

    constructor(public readonly testName: string,
                public readonly testFilePath: string,
                public readonly tags: Array<string>,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly exceptionDetail: ExceptionDetail | null,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<ReportStep>) {}

    static parse(input: Object): ReportTest {
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
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["textLogFilePath"];
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportTest(testName, testFilePath, tags, startTime, endTime, durationMillis, status, exceptionDetail, textLogFilePath, modelLogFilePath, children);
    }
}
