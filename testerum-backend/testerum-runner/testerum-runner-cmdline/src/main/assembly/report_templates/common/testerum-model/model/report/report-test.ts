import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {Path} from "../path";
import {ExecutionStatus} from "./execution-status";
import {ExceptionDetail} from "../exception/exception-detail";
import {ReportLog} from "./report-log";
import {ReportStep} from "./report-step";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";

export class ReportTest implements FeatureOrTestRunnerReportNode {

    constructor(public readonly testName: string,
                public readonly testFilePath: Path,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly exceptionDetail: ExceptionDetail | null,
                public readonly logs: Array<ReportLog>,
                public readonly children: Array<ReportStep>) {}

    static parse(input: Object): ReportTest {
        if (!input) {
            return null;
        }

        const testName = input["testName"];
        const testFilePath = Path.parse(input["testFilePath"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);
        const logs = MarshallingUtils.parseList(input["logs"], ReportLog);
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportTest(testName, testFilePath, startTime, endTime, durationMillis, status, exceptionDetail, logs, children);
    }
}
