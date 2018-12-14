import {StepCall} from "../step/call/step-call";
import {ExecutionStatus} from "./execution-status";
import {ExceptionDetail} from "../exception/exception-detail";
import {ReportLog} from "./report-log";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";

export class ReportStep {

    constructor(public readonly stepCall: StepCall,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly exceptionDetail: ExceptionDetail | null,
                public readonly logs: Array<ReportLog>,
                public readonly children: Array<ReportStep>) {}

    static parse(input: Object): ReportStep {
        const stepCall = StepCall.parse(input["stepCall"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);
        const logs = MarshallingUtils.parseList(input["logs"], ReportLog);
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportStep(stepCall, startTime, endTime, durationMillis, status, exceptionDetail, logs, children);
    }

}
