import {ReportTest} from "../../../../common/testerum-model/model/report/report-test";
import {BasicStepDef} from "../../../../common/testerum-model/model/step/def/basic-step-def";
import {ComposedStepDef} from "../../../../common/testerum-model/model/step/def/composed-step-def";
import {UndefinedStepDef} from "../../../../common/testerum-model/model/step/def/undefined-step-def";
import {ExecutionStatus} from "../../../../common/testerum-model/model/report/execution-status";

export interface TemplateTestData {
    test: ReportTest;
    BasicStepDef: typeof BasicStepDef;
    ComposedStepDef: typeof ComposedStepDef;
    UndefinedStepDef: typeof UndefinedStepDef;
    ExecutionStatus: typeof ExecutionStatus;
}
