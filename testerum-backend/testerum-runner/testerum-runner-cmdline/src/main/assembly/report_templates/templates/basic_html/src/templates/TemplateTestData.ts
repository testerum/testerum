import {ReportTest} from "../../../../common/testerum-model/report-model/model/report/report-test";
import {ReportBasicStepDef} from "../../../../common/testerum-model/report-model/model/step/def/report-basic-step-def";
import {ReportComposedStepDef} from "../../../../common/testerum-model/report-model/model/step/def/report-composed-step-def";
import {ReportUndefinedStepDef} from "../../../../common/testerum-model/report-model/model/step/def/report-undefined-step-def";
import {ExecutionStatus} from "../../../../common/testerum-model/report-model/model/report/execution-status";
import {ReportStepDef} from "../../../../common/testerum-model/report-model/model/step/def/report-step-def";

export interface TemplateTestData {
    test: ReportTest;
    ReportBasicStepDef: typeof ReportBasicStepDef;
    ReportComposedStepDef: typeof ReportComposedStepDef;
    ReportUndefinedStepDef: typeof ReportUndefinedStepDef;
    ExecutionStatus: typeof ExecutionStatus;
    stepDefsById: Map<string, ReportStepDef>;
}
