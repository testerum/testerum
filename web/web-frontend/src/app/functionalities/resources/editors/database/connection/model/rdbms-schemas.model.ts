import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class RdbmsSchemas implements Serializable<RdbmsSchemas> {

    schemas: Array<string> = [];
    errorMessage: string;

    deserialize(input: Object): RdbmsSchemas {
        for (let schema of input["schemas"] || []) {
            this.schemas.push(schema);
        }
        this.errorMessage = input["errorMessage"];
        return this;
    }

    serialize(): string {
        return null;
    }
}
