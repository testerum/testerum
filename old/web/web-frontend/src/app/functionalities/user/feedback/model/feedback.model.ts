import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class Feedback implements Serializable<Feedback> {
    name: string;
    email: string;
    message: string;

    deserialize(input: Object): Feedback {
        this.name = input["name"];
        this.email = input["email"];
        this.message = input["message"];
        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"email":' + JsonUtil.stringify(this.email) +
            ',"message":' + JsonUtil.stringify(this.message) +
            '}'
    }

    isEmpty(): boolean {
        return !(this.email || this.message);
    }
}
