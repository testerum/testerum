
import {StepPatternPart} from "../parts/step-pattern-part.model";
import {StepPatternParser} from "./step-pattern-parser";

export class ParamPatternParser {

    static parsePatternText(text: string): Array<StepPatternPart> {
        let reg = /{{(.*?)(?=}})}}/g;
        return StepPatternParser.parsePatternTextWithRegex(text, reg);
    }
}
