import {Serializable} from '../../../../model/infrastructure/serializable.model';
import {SettingType} from './setting.type.enum';
import {JsonUtil} from "../../../../utils/json.util";

export class SettingDefinition implements Serializable<SettingDefinition>{

    key: string;
    type: SettingType;
    defaultValue: string;
    label: string;
    enumValues: Array<string>;
    description: string;
    category: string;
    defined: boolean;
    uiHints: Array<string>;

    deserialize(input: Object): SettingDefinition {
        this.key = input["key"];
        this.type = SettingType[""+input["type"]];
        this.defaultValue = input["defaultValue"];
        this.label = input["label"];

        this.enumValues = [];
        for (let enumValue of (input["enumValues"] || [])) {
            this.enumValues.push(enumValue);
        }

        this.description = input["description"];
        this.category = input["category"];
        this.defined = input["defined"];

        this.uiHints = [];
        for (let uiHint of (input["uiHints"] || [])) {
            this.uiHints.push(uiHint);
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"key":' + JsonUtil.stringify(this.key) + ',' +
            '"type":' + JsonUtil.stringify(SettingType[this.type].toUpperCase())+','+
            '"defaultValue":' + JsonUtil.stringify(this.defaultValue) + ',' +
            '"label":' + JsonUtil.stringify(this.label) + ',' +
            '"enumValues":' + JsonUtil.serializeArray(this.enumValues) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"category":' + JsonUtil.stringify(this.category) + ',' +
            '"defined":' + JsonUtil.stringify(this.defined) + ',' +
            '"uiHints":' + JsonUtil.stringify(this.uiHints) +
            '}'
    }
}
