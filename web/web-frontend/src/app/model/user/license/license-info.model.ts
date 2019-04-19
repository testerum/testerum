import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {UserLicenseInfo} from "./user-license-info.model";
import {TrialLicenseInfo} from "./trial-license-info.model";

export class LicenseInfo implements Serializable<LicenseInfo>  {

    serverHasLicenses: boolean;
    currentUserLicense: UserLicenseInfo;
    trialLicense: TrialLicenseInfo;

    deserialize(input: Object): LicenseInfo {
        this.serverHasLicenses = input['serverHasLicenses'];
        this.currentUserLicense = new UserLicenseInfo().deserialize(input['currentUserLicense']);
        this.trialLicense = new TrialLicenseInfo().deserialize(input['trialLicense']);

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"serverHasLicenses":' + JsonUtil.stringify(this.serverHasLicenses) +
            ',"currentUserLicense":' + JsonUtil.serializeSerializable(this.currentUserLicense) +
            ',"trialLicense":' + JsonUtil.serializeSerializable(this.trialLicense) +
            '}'
    }
}
