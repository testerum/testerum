import {Serializable} from "../../../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../../../utils/json.util";

export class SeleniumDriverInfo implements Serializable<SeleniumDriverInfo> {
    driverVersion: string;
    browserVersions: Array<string> = [];

    deserialize(input: Object): SeleniumDriverInfo {
        this.driverVersion = input['driverVersion'];
        this.browserVersions = input['browserVersions'] || [];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"driverVersion":' + JsonUtil.stringify(this.driverVersion) +
            ',"browserVersions":' + JsonUtil.stringify(this.browserVersions) +
            '}'
    }
}
