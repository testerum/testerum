import {RunnerReportNode, RunnerReportNodeType} from "./runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";
import {ReportTest} from "./report-test";
import {ReportFeature} from "./report-feature";
import {ReportStepDef, StepDefType} from "../step/def/report-step-def";
import {ReportBasicStepDef} from "../step/def/report-basic-step-def";
import {ReportComposedStepDef} from "../step/def/report-composed-step-def";
import {ReportUndefinedStepDef} from "../step/def/report-undefined-step-def";

export class ReportSuite implements RunnerReportNode {

    constructor(public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<FeatureOrTestRunnerReportNode>,
                public readonly stepDefsById: Map<string, ReportStepDef>) { }

    // noinspection JSMethodCanBeStatic
    get name(): string {
        return "Test Suite";
    }

    static parse(input: Object): ReportSuite {
        if (!input) {
            return null;
        }

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

        const stepDefsById = MarshallingUtils.parseMapPolymorficaly<ReportStepDef>(input["stepDefsById"], {
            [StepDefType[StepDefType.BASIC_STEP]]: ReportBasicStepDef,
            [StepDefType[StepDefType.COMPOSED_STEP]]: ReportComposedStepDef,
            [StepDefType[StepDefType.UNDEFINED_STEP]]: ReportUndefinedStepDef

        });

        return new ReportSuite(startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children, stepDefsById);
    }
}
