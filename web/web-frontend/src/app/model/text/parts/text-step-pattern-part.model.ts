
import {StepPatternPart} from "./step-pattern-part.model";
import {JsonUtil} from "../../../utils/json.util";

export class TextStepPatternPart implements StepPatternPart, Serializable<TextStepPatternPart>{

    text:string;

    deserialize(input: Object): TextStepPatternPart {
        this.text = input["text"];
        return this;
    }

    serialize(): string {
        return ""+
            '{' +
            '"@type": "TEXT",'+
            '"text":'+JsonUtil.stringify(this.text) +
            '}'
    }
}