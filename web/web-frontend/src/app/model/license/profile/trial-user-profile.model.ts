import {UserProfile} from "./user-profile.model";
import {Serializable} from "../../infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {UserProfileTypes} from "./user-profile-types";

export class TrialUserProfile implements UserProfile, Serializable<TrialUserProfile> {

    startDate: Date;
    endDate: Date;

    deserialize(input: Object): TrialUserProfile {
        this.startDate = new Date(input["startDate"]);
        this.endDate = new Date(input["endDate"]);

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type": "' + UserProfileTypes.TRIAL + '",' +
            '"startDate":' + JsonUtil.serializeDateWithoutTime(this.startDate) + ',' +
            '"endDate":' + JsonUtil.serializeDateWithoutTime(this.endDate) +
            '}';
    }

}
