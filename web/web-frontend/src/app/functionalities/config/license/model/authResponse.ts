import {JsonUtil} from "../../../../utils/json.util";
import {Serializable} from "../../../../model/infrastructure/serializable.model";

export class AuthResponse implements Serializable<AuthResponse> {
    email: string;
    name: string;
    companyName: string;
    licenseExpirationDate: Date;
    authToken: string;

    deserialize(input: Object): AuthResponse {
        this.email = input["email"];
        this.name = input["name"];
        this.companyName = input["companyName"];
        this.licenseExpirationDate = input["licenseExpirationDate"];
        this.authToken = input["authToken"];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"email":' + JsonUtil.stringify(this.email) +
            ',"name":' + JsonUtil.stringify(this.name) +
            ',"companyName":' + JsonUtil.stringify(this.companyName) +
            ',"licenseExpirationDate":' + JsonUtil.stringify(this.licenseExpirationDate) +
            ',"authToken":' + JsonUtil.stringify(this.authToken) +
            '}'
    }
}
