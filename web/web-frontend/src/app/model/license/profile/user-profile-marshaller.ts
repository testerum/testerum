import {UserProfile} from "./user-profile.model";
import {UserProfileTypes} from "./user-profile-types";
import {TrialUserProfile} from "./trial-user-profile.model";

export class UserProfileMarshaller {

    static deserialize(input: Object): UserProfile {
        if (!input) {
            return null;
        }

        const type = input["@type"];
        if (type === UserProfileTypes.TRIAL) {
            return new TrialUserProfile().deserialize(input);
        }

        throw new Error(`unknown user profile type [${type}]`);
    }

}
