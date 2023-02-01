import {Serializable} from "./serializable.model";

export class Config implements Serializable<Config> {
    key: string;
    values: Array<string> = [];

    deserialize(input: Object): Config {
        this.key = input.toString();
        return this;
    }

    serialize(): string {
        return null;
    }
}
