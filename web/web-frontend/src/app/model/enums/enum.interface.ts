import {Serializable} from "../infrastructure/serializable.model";

export abstract class Enum implements Serializable<Enum> {

    readonly enumAsString: string;
    constructor(enumAsString: string) {
        this.enumAsString = enumAsString;
    }

    toString(): string {
        return this.enumAsString
    }

    deserialize(input: Object): Enum {
        throw new Error("Not implemented");
    }

    serialize(): string {
        return this.enumAsString;
    }
}
