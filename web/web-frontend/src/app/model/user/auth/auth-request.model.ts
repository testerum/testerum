import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";

export class AuthRequest implements Serializable<AuthRequest>  {

    email: string;
    password: string;

    deserialize(input: Object): AuthRequest {
        this.email = input['email'];
        this.password = input['password'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"email":' + JsonUtil.stringify(this.email) +
            ',"password":' + JsonUtil.stringify(this.password) +
            '}'
    }
}
