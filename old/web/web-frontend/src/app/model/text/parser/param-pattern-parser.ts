
import {StepPatternPart} from "../parts/step-pattern-part.model";
import {StepPatternParser} from "./step-pattern-parser";

export class ParamPatternParser {
    static readonly TESTERUM_EXPRESION_PATTERN = /{{(.*?)(?=}})}}/g;

    static parsePatternText(text: string): Array<StepPatternPart> {
        let reg = ParamPatternParser.TESTERUM_EXPRESION_PATTERN;
        return StepPatternParser.parsePatternTextWithRegex(text, reg);
    }
}
