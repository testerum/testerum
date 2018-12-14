export class TextStepPatternPart {

    constructor(public readonly text: string) {}

    static parse(input: Object): TextStepPatternPart {
        const text = input["text"];

        return new TextStepPatternPart(text);
    }

}
