import {Serializable} from "../../../../model/infrastructure/serializable.model";

export class UserProfile implements Serializable<UserProfile> {

    name: string;
    version: string;
    license: string;
    expirationDate: Date;

    deserialize(input: Object): UserProfile {
        this.name = input["name"];
        this.license = input["license"];
        this.version = input["version"];
        this.expirationDate = input["expirationDate"];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented");
    }

}
