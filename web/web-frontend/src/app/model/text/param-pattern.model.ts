import {StepsPackageModel} from "../steps-package.model";
import {StepPatternParser} from "./parser/step-pattern-parser";
import {StepPattern} from "./step-pattern.model";
import {ParamPatternParser} from "./parser/param-pattern-parser";
import {TextStepPatternPart} from "./parts/text-step-pattern-part.model";
import {ParamStepPatternPart} from "./parts/param-step-pattern-part.model";

export class ParamPattern extends StepPattern {

    setPatternText(text: string) {
        this.patternParts.length = 0;
        this.patternParts = this.patternParts.concat (
            ParamPatternParser.parsePatternText(text)
        );
    }

    getPatternText(): string {
        let result = "";
        for (let patternPart of this.patternParts) {
            if (patternPart instanceof TextStepPatternPart) {
                result += patternPart.text;
            } else if (patternPart instanceof ParamStepPatternPart) {
                result += "{{" + patternPart.name + "}}"
            } else {
                throw new Error("Unrecognized StepPatternPart")
            }
        }
        return result;
    }
}
