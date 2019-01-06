import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class Feedback implements Serializable<Feedback> {
    email: string;
    subject: string;
    description: string;

    deserialize(input: Object): Feedback {
        this.email = input["email"];
        this.subject = input["subject"];
        this.description = input["description"];
        return this;
    }

    serialize(): string {
        if(!this.email && !this.subject && !this.description) {
            return "";
        }

        let response = '{';
        response += '"email":' + JsonUtil.stringify(this.email);
        response += ',"subject":' + JsonUtil.stringify(this.subject);
        response += ',"description":' + JsonUtil.stringify(this.description);

        response += '}';
        return response;
    }

    isEmpty(): boolean {
        return !(this.subject || this.description);
    }
}
