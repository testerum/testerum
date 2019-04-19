import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";

export class TrialLicenseInfo implements Serializable<TrialLicenseInfo>  {

    startDate: Date;
    endDate: Date;
    expired: boolean;

    deserialize(input: Object): TrialLicenseInfo {
        this.startDate = new Date(input['startDate']);
        this.endDate = new Date(input['endDate']);
        this.expired = input['expired'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"startDate":' + JsonUtil.stringify(this.startDate ? this.startDate.toJSON() : null) +
            ',"endDate":' + JsonUtil.stringify(this.endDate ? this.endDate.toJSON() : null) +
            ',"expired":' + JsonUtil.stringify(this.expired) +
            '}'
    }
}
