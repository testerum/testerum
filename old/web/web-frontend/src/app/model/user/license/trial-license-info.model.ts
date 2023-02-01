import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";

export class TrialLicenseInfo implements Serializable<TrialLicenseInfo>  {

    startDate: Date;
    endDate: Date;
    daysUntilExpiration: number;
    expired: boolean;

    deserialize(input: Object): TrialLicenseInfo {
        if(!input) return null;

        this.startDate = new Date(input['startDate']);
        this.endDate = new Date(input['endDate']);
        this.expired = input['expired'];
        this.daysUntilExpiration = input['daysUntilExpiration'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"startDate":' + JsonUtil.stringify(this.startDate ? this.startDate.toJSON() : null) +
            ',"endDate":' + JsonUtil.stringify(this.endDate ? this.endDate.toJSON() : null) +
            ',"daysUntilExpiration":' + JsonUtil.stringify(this.daysUntilExpiration) +
            ',"expired":' + JsonUtil.stringify(this.expired) +
            '}'
    }
}
