import {RdbmsField} from "./rdbms-field.model";
import {Serializable} from "../../../infrastructure/serializable.model";

export class RdbmsTable implements Serializable<RdbmsTable> {

    name: string;
    type: RdbmsTableType;
    comment: string;
    fields: Array<RdbmsField> = [];

    deserialize(input: Object): RdbmsTable {
        this.name = input["name"];
        this.type = RdbmsTableType[""+input["type"]];
        this.comment = input["comment"];

        for (let fieldAsJson of input["fields"]) {
            let field = new RdbmsField().deserialize(fieldAsJson);
            this.fields.push(field)
        }

        return this;
    }

    serialize(): string {
        return undefined;
    }

    containsField(fieldName: string): boolean {
        for (let field of this.fields) {
            if(field.name === fieldName) {
                return true;
            }
        }
        return false;
    }

}

enum RdbmsTableType {
    PERMANENT,
    SYSTEM_TABLE,
    GLOBAL_TEMPORARY,
    LOCAL_TEMPORARY,
    VIEW
}
