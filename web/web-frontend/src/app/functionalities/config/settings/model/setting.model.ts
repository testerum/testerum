import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {SettingDefinition} from './setting-definition.model';
import {JsonUtil} from "../../../../utils/json.util";

export class Setting implements Serializable<Setting> {

    definition: SettingDefinition;
    unresolvedValue: string;
    resolvedValue: string;

    deserialize(input: Object): Setting {
        this.definition = new SettingDefinition().deserialize(input["definition"]);
        this.unresolvedValue = input["unresolvedValue"];
        this.resolvedValue = input["resolvedValue"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"definition":' + JsonUtil.serializeSerializable(this.definition) + ',' +
            '"unresolvedValue":' + JsonUtil.stringify(this.unresolvedValue) + ',' +
            '"resolvedValue":' + JsonUtil.stringify(this.resolvedValue) +
            '}'
    }

    clone(): Setting {
        return new Setting().deserialize(JSON.parse(this.serialize()));
    }
}
