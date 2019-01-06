import {ReportStepDef} from "./report-step-def";
import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";

export class ReportUndefinedStepDef implements ReportStepDef {

    constructor(public readonly id: string,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern) { }

    static parse(input: Object): ReportUndefinedStepDef {
        const id = input["id"];
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);

        return new ReportUndefinedStepDef(id, phase, stepPattern);
    }

}
