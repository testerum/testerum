import {TextStepPatternPart} from "../parts/text-step-pattern-part.model";
import {ParamStepPatternPart} from "../parts/param-step-pattern-part.model";
import {StepPatternPart} from "../parts/step-pattern-part.model";
import {ResourceMapEnum} from "../../../functionalities/resources/editors/resource-map.enum";

export class StepPatternParser {

    static parsePatternText(text: string): Array<StepPatternPart> {
        let reg: RegExp = /<<(.*?)(?=>>)>>/g;
        return this.parsePatternTextWithRegex(text, reg)
    }

    static parsePatternTextWithRegex(text: string, reg: RegExp): Array<StepPatternPart> {
        let patternParts: Array<StepPatternPart> = [];


        let textStartIndex: number = 0;
        let match: RegExpExecArray;
        while (match = reg.exec(text)) {
            if (textStartIndex < match.index) {
                let textPart: TextStepPatternPart = this.getTextPattern(text, textStartIndex, match.index);
                patternParts.push(textPart);
            }

            let paramPart: ParamStepPatternPart = this.getParamPattern(match);
            patternParts.push(paramPart);

            textStartIndex = match.index + paramPart.name.length + 4;
        }

        if (text && textStartIndex < text.length) {
            let textPart: TextStepPatternPart = this.getTextPattern(text, textStartIndex, text.length);
            patternParts.push(textPart);
        }

        return patternParts;
    }

    private static getParamPattern(match: RegExpExecArray): ParamStepPatternPart {
        let paramPart: ParamStepPatternPart = new ParamStepPatternPart;
        paramPart.name = match[1];
        paramPart.serverType = ResourceMapEnum.TEXT.serverType;
        paramPart.uiType = ResourceMapEnum.TEXT.uiType;
        return paramPart;
    }

    private static getTextPattern(text: string, textStartIndex: number, index: number): TextStepPatternPart {
        let textPart = new TextStepPatternPart();
        textPart.text = text.substr(textStartIndex, index - textStartIndex);
        return textPart;
    }
}
