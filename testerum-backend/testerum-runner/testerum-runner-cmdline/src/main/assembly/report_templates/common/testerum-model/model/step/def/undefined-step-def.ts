import {StepDef} from "./step-def";
import {Path} from "../../path";
import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";

export class UndefinedStepDef implements StepDef {

    constructor(public readonly id: string,
                public readonly path: Path,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern,
                public readonly description: string | null,
                public readonly tags: Array<string>) { }

    static parse(input: Object): UndefinedStepDef {
        const id = input["id"];
        const path = Path.parse(input["path"]);
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);
        const description = input["description"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);

        return new UndefinedStepDef(id, path, phase, stepPattern, description, tags);
    }

}
