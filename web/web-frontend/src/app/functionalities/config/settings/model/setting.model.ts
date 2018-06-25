import {SettingTypeEnum} from "./setting-type.enum";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {JsonUtil} from "../../../../utils/json.util";

export class Setting implements Serializable<Setting> {

    key: string;
    unresolvedValue: string;
    value: string;
    type: SettingTypeEnum;
    defaultValue: string;
    description: string;
    category: string;

    deserialize(input: Object): Setting {
        this.key = input["key"];
        this.unresolvedValue = input["unresolvedValue"];
        this.value = input["value"];
        this.type = SettingTypeEnum[""+input["type"]];
        this.defaultValue = input["defaultValue"];
        this.description = input["description"];
        this.category = input["category"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"key":' + JsonUtil.stringify(this.key) + ',' +
            '"unresolvedValue":' + JsonUtil.stringify(this.unresolvedValue) + ',' +
            '"value":' + JsonUtil.stringify(this.value) + ',' +
            '"type":' + JsonUtil.stringify(SettingTypeEnum[this.type].toUpperCase()) + ',' +
            '"defaultValue":' + JsonUtil.stringify(this.defaultValue) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"category":' + JsonUtil.stringify(this.category) +
            '}'
    }
}
