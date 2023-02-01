import {ReportStepDef} from "./report-step-def";
import {Path} from "../../path";
import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {ReportStepCall} from "../call/report-step-call";
import {MarshallingUtils} from "../../../../json-marshalling/marshalling-utils";

export class ReportComposedStepDef implements ReportStepDef {

    constructor(public readonly id: string,
                public readonly path: Path,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern,
                public readonly description: string | null,
                public readonly tags: Array<string>,
                public readonly stepCalls: Array<ReportStepCall>) { }

    static parse(input: Object): ReportComposedStepDef {
        const id = input["id"];
        const path = Path.createInstance(input["path"]);
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);
        const description = input["description"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const stepCalls = MarshallingUtils.parseList(input["stepCalls"], ReportStepCall);

        return new ReportComposedStepDef(id, path, phase, stepPattern, description, tags, stepCalls);
    }
}
