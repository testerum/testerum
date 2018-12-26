import {ReportTest} from "../../../../common/testerum-model/model/report/report-test";
import {ExecutionStatus} from "../../../../common/testerum-model/model/report/execution-status";
import {ReportBasicStepDef} from "../../../../common/testerum-model/model/step/def/report-basic-step-def";
import {ReportComposedStepDef} from "../../../../common/testerum-model/model/step/def/report-composed-step-def";
import {ReportUndefinedStepDef} from "../../../../common/testerum-model/model/step/def/report-undefined-step-def";

export interface TemplateTestData {
    test: ReportTest;
    BasicStepDef: typeof ReportBasicStepDef;
    ComposedStepDef: typeof ReportComposedStepDef;
    UndefinedStepDef: typeof ReportUndefinedStepDef;
    ExecutionStatus: typeof ExecutionStatus;
}
