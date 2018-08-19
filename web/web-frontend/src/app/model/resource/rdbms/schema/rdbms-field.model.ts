import {Serializable} from "../../../infrastructure/serializable.model";

export class RdbmsField implements Serializable<RdbmsField> {
    name: string;
    comment: string;
    isMandatory: boolean;

    deserialize(input: Object): RdbmsField {
        this.name = input["name"];
        this.comment = input["comment"];
        this.isMandatory = input["isMandatory"];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
