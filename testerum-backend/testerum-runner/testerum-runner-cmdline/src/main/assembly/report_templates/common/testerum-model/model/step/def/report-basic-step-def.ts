import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {ReportStepDef} from "./report-step-def";
import {Path} from "../../path";

export class ReportBasicStepDef implements ReportStepDef {

    constructor(public readonly id: string,
                public readonly path: Path,
                public readonly phase: StepPhaseEnum,
                public readonly stepPattern: StepPattern,
                public readonly description: string | null,
                public readonly tags: Array<string>,
                public readonly className: string,
                public readonly methodName: string) { }

    static parse(input: Object): ReportBasicStepDef {
        const id = input["id"];
        const path = Path.createInstance(input["path"]);
        const phase = MarshallingUtils.parseEnum(input["phase"], StepPhaseEnum);
        const stepPattern = StepPattern.parse(input["stepPattern"]);
        const description = input["description"];
        const tags = MarshallingUtils.parseListOfStrings(input["tags"]);
        const className = input["className"];
        const methodName = input["methodName"];

        return new ReportBasicStepDef(id, path, phase, stepPattern, description, tags, className, methodName);
    }

}
