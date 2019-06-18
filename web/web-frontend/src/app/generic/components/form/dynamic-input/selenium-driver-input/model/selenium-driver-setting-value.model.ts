import {SeleniumBrowserType} from "./selenium-browser-type.enum";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../../../utils/json.util";

export class SeleniumDriverSettingValue implements Serializable<SeleniumDriverSettingValue> {
    browserType: SeleniumBrowserType;
    browserExecutablePath: string;
    headless: boolean;
    driverVersion: string;
    remoteUrl: string | null;

    deserialize(input: Object): SeleniumDriverSettingValue {
        this.browserType = SeleniumBrowserType.fromSerialization(input["browserType"]);
        this.browserExecutablePath = input["browserExecutablePath"];
        this.headless = input["headless"];
        this.driverVersion = input["driverVersion"];
        this.remoteUrl = input["remoteUrl"];

        return this;
    }

    serialize(): string {
        return '' +
            '{' +
            '"browserType":' + (this.browserType ? JsonUtil.stringify(this.browserType.asSerialized) : JsonUtil.stringify(null)) +
            ',"browserExecutablePath":' + JsonUtil.stringify(this.browserExecutablePath) +
            ',"headless":' + JsonUtil.stringify(this.headless) +
            ',"driverVersion":' + JsonUtil.stringify(this.driverVersion) +
            ',"remoteUrl":' + (this.remoteUrl ? JsonUtil.stringify(this.remoteUrl) : JsonUtil.stringify(null)) +
            '}';
    }
}
