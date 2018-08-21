import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {SettingDefinition} from './setting-definition.model';

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
        throw Error("method not implemented");
    }

}
