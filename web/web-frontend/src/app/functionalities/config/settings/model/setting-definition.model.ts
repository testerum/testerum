import {Serializable} from '../../../../model/infrastructure/serializable.model';
import {SettingType} from './setting.type.enum';

export class SettingDefinition implements Serializable<SettingDefinition>{

    key: string;
    type: SettingType;
    defaultValue: string;
    label: string;
    description: string;
    category: string;
    defined: boolean;

    deserialize(input: Object): SettingDefinition {
        this.key = input["key"];
        this.type = SettingType[""+input["type"]];
        this.defaultValue = input["defaultValue"];
        this.label = input["label"];
        this.description = input["description"];
        this.category = input["category"];
        this.defined = input["defined"];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented");
    }

}
