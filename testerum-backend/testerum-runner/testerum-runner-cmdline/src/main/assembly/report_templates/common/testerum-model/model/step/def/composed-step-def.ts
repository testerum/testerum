import {StepDef} from "./step-def";
import {Path} from "../../path";
import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {StepCall} from "../call/step-call";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";

export class ComposedStepDef implements StepDef {

    constructor(public readonly id: string,
                public readonly path: Path,
                public readonly oldPath: Path | null,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern,
                public readonly description: string | null,
                public readonly tags: Array<string>,
                public readonly stepCalls: Array<StepCall>) { }

    static parse(input: Object): ComposedStepDef {
        const id = input["id"];
        const path = Path.parse(input["path"]);
        const oldPath = Path.parse(input["oldPath"]);
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);
        const description = input["description"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const stepCalls = MarshallingUtils.parseList(input["stepCalls"], StepCall);

        return new ComposedStepDef(id, path, oldPath, phase, stepPattern, description, tags, stepCalls);
    }
}
