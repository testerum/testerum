import {RunnerReportNode, RunnerReportNodeType} from "./runner-report-node";
import {ExecutionStatus} from "./execution-status";
import {FeatureOrTestRunnerReportNode} from "./feature-or-test-runner-report-node";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {ReportTest} from "./report-test";
import {ReportFeature} from "./report-feature";
import {ReportStepDef, StepDefType} from "../step/def/report-step-def";
import {ReportBasicStepDef} from "../step/def/report-basic-step-def";
import {ReportComposedStepDef} from "../step/def/report-composed-step-def";
import {ReportUndefinedStepDef} from "../step/def/report-undefined-step-def";
import {ReportParametrizedTest} from "./report-parametrized-test";
import {ReportScenario} from "./report-scenario";

export class ReportSuite implements RunnerReportNode {

    constructor(public readonly executionName: string | null,
                public readonly startTime: Date,
                public readonly endTime: Date,
                public readonly durationMillis: number,
                public readonly status: ExecutionStatus,
                public readonly textLogFilePath: string,
                public readonly modelLogFilePath: string,
                public readonly children: Array<FeatureOrTestRunnerReportNode>,
                public readonly stepDefsById: Map<string, ReportStepDef>) { }

    static parse(input: Object): ReportSuite {
        if (!input) {
            return null;
        }

        const executionName = input["executionName"] || null;
        const startTime = MarshallingUtils.parseUtcToLocalDateTime(input["startTime"]);
        const endTime = MarshallingUtils.parseUtcToLocalDateTime(input["endTime"]);
        const durationMillis = input["durationMillis"];
        const status = MarshallingUtils.parseEnum(input["status"], ExecutionStatus);
        const textLogFilePath = input["textLogFilePath"];
        const modelLogFilePath = input["modelLogFilePath"];

        const children = MarshallingUtils.parseListPolymorphically<FeatureOrTestRunnerReportNode>(input["children"], {
            [RunnerReportNodeType[RunnerReportNodeType.FEATURE]]: ReportFeature,
            [RunnerReportNodeType[RunnerReportNodeType.TEST]]: ReportTest,
            [RunnerReportNodeType[RunnerReportNodeType.PARAMETRIZED_TEST]]: ReportParametrizedTest,
            [RunnerReportNodeType[RunnerReportNodeType.SCENARIO]]: ReportScenario
        });

        const stepDefsById = MarshallingUtils.parseMapPolymorficaly<ReportStepDef>(input["stepDefsById"], {
            [StepDefType[StepDefType.BASIC_STEP]]: ReportBasicStepDef,
            [StepDefType[StepDefType.COMPOSED_STEP]]: ReportComposedStepDef,
            [StepDefType[StepDefType.UNDEFINED_STEP]]: ReportUndefinedStepDef
        });

        return new ReportSuite(executionName, startTime, endTime, durationMillis, status, textLogFilePath, modelLogFilePath, children, stepDefsById);
    }
}
