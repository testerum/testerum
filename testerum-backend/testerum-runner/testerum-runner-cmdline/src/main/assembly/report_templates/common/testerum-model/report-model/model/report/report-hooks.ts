import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {HookPhase} from "./hook-phase";
import {ReportStep} from "./report-step";

export class ReportHooks implements FeatureOrTestRunnerReportNode {

    constructor(public readonly hookPhase: HookPhase,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<ReportStep>) {
    }

    static parse(input: Object): ReportHooks {
        if (!input) {
            return null;
        }

        const hookPhase = MarshallingUtils.parseEnum(input["hookPhase"], HookPhase);
        const startTime = MarshallingUtils.parseUtcToLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseUtcToLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];
        const children = MarshallingUtils.parseList(input["children"], ReportStep);

        return new ReportHooks(hookPhase, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children);
    }

}
