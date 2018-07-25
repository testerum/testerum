import {WarningType} from "./WarningType";

export class Warning implements Serializable<Warning> {

    type: WarningType;
    message: String;

    deserialize(input: Object): Warning {
        this.type = WarningType["" + input['type']];
        this.message = input['message'];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
