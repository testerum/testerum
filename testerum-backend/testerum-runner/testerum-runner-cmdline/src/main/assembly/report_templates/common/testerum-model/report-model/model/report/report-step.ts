import {ReportStepCall} from "../step/call/report-step-call";
import {ExecutionStatus} from "./execution-status";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {RunnerReportNode} from "./runner-report-node";

export class ReportStep implements RunnerReportNode {

    constructor(public readonly stepCall: ReportStepCall,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<ReportStep>) {}

    static parse(input: Object): ReportStep {
        const stepCall = ReportStepCall.parse(input["stepCall"]);
        const startTime = MarshallingUtils.parseLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportStep(stepCall, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children);
    }

}
