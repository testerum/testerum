import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {StepDef} from "./step-def";
import {Path} from "../../path";

export class BasicStepDef implements StepDef {

    constructor(public readonly id: string,
                public readonly path: Path,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern,
                public readonly description: string | null,
                public readonly tags: Array<string>,
                public readonly className: string,
                public readonly methodName: string) { }

    static parse(input: Object): BasicStepDef {
        const id = input["id"];
        const path = Path.parse(input["path"]);
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);
        const description = input["description"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const className = input["className"];
        const methodName = input["methodName"];

        return new BasicStepDef(id, path, phase, stepPattern, description, tags, className, methodName);
    }

}
