import {Serializable} from '../../../../model/infrastructure/serializable.model';
import {SettingType} from './setting.type.enum';

export class SettingDefinition implements Serializable<SettingDefinition>{

    key: string;
    type: SettingType;
    defaultValue: string;
    label: string;
    enumValues: Array<String>;
    description: string;
    category: string;
    defined: boolean;

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

        return this;
    }

    serialize(): string {
        throw Error("method not implemented");
    }

}
