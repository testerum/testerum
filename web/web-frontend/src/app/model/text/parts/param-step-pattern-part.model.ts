import {StepPatternPart} from "./step-pattern-part.model";
import {JsonUtil} from "../../../utils/json.util";
import {ServerToUiTypeMapperUtil} from "../../../utils/server-to-ui-type-mapper.util";

export class ParamStepPatternPart implements StepPatternPart, Serializable<ParamStepPatternPart>{

    name: string;
    serverType: string;
    uiType: string;
    description: string;
    enumValues: Array<string> = [];

    deserialize(input: Object): ParamStepPatternPart {
        this.name = input["name"];
        this.serverType = input["type"];
        this.uiType = ServerToUiTypeMapperUtil.mapServerToUi(this.serverType);
        this.description = input["description"];

        for (let allowedValuesJson of input["enumValues"] || []) {
            this.enumValues.push(allowedValuesJson);
        }

        return this;
    }

    serialize(): string {
        return ""+
            '{' +
            '"@type": "PARAM",'+
            '"name":'+JsonUtil.stringify(this.name) +','+
            '"type":'+JsonUtil.stringify(this.uiType) +','+
            '"description":'+JsonUtil.stringify(this.description) +','+
            '"enumValues":'+JsonUtil.serializeArray(this.enumValues)+
            '}'
    }
}
