import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {ScenarioParam} from "./param/scenario-param.model";

export class Scenario implements Serializable<Scenario> {

    name: string;
    params: Array<ScenarioParam> = [];
    enabled: boolean;

    deserialize(input: Object): Scenario {
        this.name = input['name'];

        this.params = [];
        for (let paramsJson of (input['params'] || [])) {
            this.params.push(
                new ScenarioParam().deserialize(paramsJson)
            );
        }

        this.enabled = input['enabled'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"params":' + JsonUtil.serializeArrayOfSerializable(this.params) +
            ',"enabled":' + JsonUtil.stringify(this.enabled) +
            '}'
    }

    clone(): Scenario {
        return new Scenario().deserialize(JSON.parse(this.serialize()));
    }
}
