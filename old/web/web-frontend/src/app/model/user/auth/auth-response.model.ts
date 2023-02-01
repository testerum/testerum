import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {UserLicenseInfo} from "../license/user-license-info.model";

export class AuthResponse implements Serializable<AuthResponse>  {

    authToken: string;
    currentUserLicense: UserLicenseInfo;

    deserialize(input: Object): AuthResponse {
        this.authToken = input['authToken'];
        this.currentUserLicense = new UserLicenseInfo().deserialize(input['currentUserLicense']);

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"authToken":' + JsonUtil.stringify(this.authToken) +
            ',"currentUserLicense":' + JsonUtil.serializeSerializable(this.currentUserLicense) +
            '}'
    }
}
