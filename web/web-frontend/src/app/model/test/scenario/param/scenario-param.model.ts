import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ScenarioParamType} from "./scenario-param-type.enum";

export class ScenarioParam implements Serializable<ScenarioParam> {

    name: string;
    type: ScenarioParamType = ScenarioParamType.TEXT;
    value: string;

    deserialize(input: Object): ScenarioParam {
        this.name = input['name'];
        if(input['type']) {
            this.type = ScenarioParamType.fromString(input['type']);
        }
        this.value = input['value'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"type":' + JsonUtil.stringify(this.type.toString()) +
            ',"value":' + JsonUtil.stringify(this.value) +
            '}'
    }
}
