import {MarshallingUtils} from "../../../../../../json-marshalling/marshalling-utils";

export class ParamStepPatternPart {

    constructor(public readonly name: string,
                public readonly type: string,
                public readonly description: string|null,
                public readonly enumValues: Array<string>) { }

    static parse(input: Object): ParamStepPatternPart {
        const name = input["name"];
        const type = input["type"];
        const description = input["description"];
        const enumValues = MarshallingUtils.parseListOfStrings(input["enumValues"]);

        return new ParamStepPatternPart(name, type, description, enumValues);
    }

}
