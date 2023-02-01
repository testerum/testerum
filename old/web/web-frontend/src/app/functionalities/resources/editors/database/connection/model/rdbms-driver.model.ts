import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class RdbmsDriver implements Serializable<RdbmsDriver> {

    name: string;
    driverJar: string;
    driverClass: string;
    urlPattern: string;

    deserialize(input: Object): RdbmsDriver {
        this.name = input["name"];
        this.driverJar = input["driverJar"];
        this.driverClass = input["driverClass"];
        this.urlPattern = input["urlPattern"];
        return this;
    }

    serialize(): string {
        return null;
    }
}
