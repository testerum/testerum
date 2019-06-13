import {SeleniumBrowserType} from "./selenium-browser-type.enum";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../../../utils/json.util";

export class SeleniumDriverSettingValue implements Serializable<SeleniumDriverSettingValue> {
    browserType: SeleniumBrowserType;
    driverExecutablePath: string;
    headless: boolean;
    driverVersion: string;

    deserialize(input: Object): SeleniumDriverSettingValue {
        this.browserType = SeleniumBrowserType.fromSerialization(input["browserType"]);
        this.driverExecutablePath = input["driverExecutablePath"];
        this.headless = input["headless"];
        this.driverVersion = input["driverVersion"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"browserType":' + this.browserType ? this.browserType.asSerialized : "null" +
            ',"driverExecutablePath":' + JsonUtil.stringify(this.driverExecutablePath) +
            ',"headless":' + JsonUtil.stringify(this.headless) +
            ',"driverVersion":' + JsonUtil.stringify(this.driverVersion) +
            '}'
    }
}
