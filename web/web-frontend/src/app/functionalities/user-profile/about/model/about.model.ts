import {Serializable} from "../../../../model/infrastructure/serializable.model";

export class About implements Serializable<About> {

    name: string;
    version: string;
    license: string;
    expirationDate: Date; //TODO set property as date

    deserialize(input: Object): About {
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
