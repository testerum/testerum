import {JsonUtil} from "../../../utils/json.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class UserLicenseInfo implements Serializable<UserLicenseInfo>  {

    email: string;
    firstName: string;
    lastName: string;
    creationDate: Date;
    expirationDate: Date;
    daysUntilExpiration: number;
    expired: boolean;

    deserialize(input: Object): UserLicenseInfo {
        if(!input) return null;

        this.email = input['email'];
        this.firstName = input['firstName'];
        this.lastName = input['lastName'];
        this.creationDate = new Date(input['creationDate']);
        this.expirationDate = new Date(input['expirationDate']);
        this.daysUntilExpiration = input['daysUntilExpiration'];
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
            ',"daysUntilExpiration":' + JsonUtil.stringify(this.daysUntilExpiration) +
            ',"expired":' + JsonUtil.stringify(this.expired) +
            '}'
    }
}
