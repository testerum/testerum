import {UserProfile} from "./user-profile.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {UserProfileTypes} from "./user-profile-types";

export class TrialUserProfile implements UserProfile, Serializable<TrialUserProfile> {

    startDateUtc: Date;
    endDateUtc: Date;

    deserialize(input: Object): TrialUserProfile {
        this.startDateUtc = new Date(input["startDateUtc"]);
        this.endDateUtc = new Date(input["endDateUtc"]);

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type": "' + UserProfileTypes.TRIAL + '",' +
            '"startDateUtc":' + JsonUtil.serializeDateWithoutTime(this.startDateUtc) + ',' +
            '"endDateUtc":' + JsonUtil.serializeDateWithoutTime(this.endDateUtc) +
            '}';
    }

}
