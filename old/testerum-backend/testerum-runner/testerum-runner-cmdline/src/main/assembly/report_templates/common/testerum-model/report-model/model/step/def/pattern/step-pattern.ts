import {StepPatternPart, StepPatternPartType} from "./part/step-pattern-part";
import {MarshallingUtils} from "../../../../../json-marshalling/marshalling-utils";
import {ParamStepPatternPart} from "./part/param-step-pattern-part";
import {TextStepPatternPart} from "./part/text-step-pattern-part";

export class StepPattern {

    constructor(public readonly patternParts: Array<StepPatternPart>) {}

    static parse(input: Object): StepPattern {
        const patternParts = MarshallingUtils.parseListPolymorphically<StepPatternPart>(input["patternParts"], {
            [StepPatternPartType[StepPatternPartType.PARAM]]: ParamStepPatternPart,
            [StepPatternPartType[StepPatternPartType.TEXT]]: TextStepPatternPart
        });

        return new StepPattern(patternParts);
    }

}
