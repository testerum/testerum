import {JsonUtil} from "../../../utils/json.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class UserLicenseInfo implements Serializable<UserLicenseInfo>  {

    email: string;
    firstName: string;
    lastName: string;
    creationDate: Date;
    expirationDate: Date;
    expired: boolean;

    deserialize(input: Object): UserLicenseInfo {
        this.email = input['email'];
        this.firstName = input['firstName'];
        this.lastName = input['lastName'];
        this.creationDate = new Date(input['creationDate']);
        this.expirationDate = new Date(input['expirationDate']);
        this.expired = input['expired'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"email":' + JsonUtil.stringify(this.email) +
            ',"firstName":' + JsonUtil.stringify(this.firstName) +
            ',"lastName":' + JsonUtil.stringify(this.lastName) +
            ',"creationDate":' + JsonUtil.stringify(this.creationDate ? this.creationDate.toJSON() : null) +
            ',"expirationDate":' + JsonUtil.stringify(this.expirationDate ? this.expirationDate.toJSON() : null) +
            ',"expired":' + JsonUtil.stringify(this.expired) +
            '}'
    }
}
