import {ReportStepCall} from "../step/call/report-step-call";
import {ExecutionStatus} from "./execution-status";
import {ExceptionDetail} from "../exception/exception-detail";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";
import {RunnerReportNode} from "./runner-report-node";

export class ReportStep implements RunnerReportNode {

    constructor(public readonly stepCall: ReportStepCall,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly exceptionDetail: ExceptionDetail | null,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<ReportStep>) {}

    static parse(input: Object): ReportStep {
        const stepCall = ReportStepCall.parse(input["stepCall"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["textLogFilePath"];
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportStep(stepCall, startTime, endTime, durationMillis, status, exceptionDetail, textLogFilePath, modelLogFilePath, children);
    }

}
