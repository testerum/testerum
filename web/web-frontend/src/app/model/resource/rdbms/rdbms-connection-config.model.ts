
import {JsonUtil} from "../../../utils/json.util";
import {NameProperty} from "../interface/name-property.model";
import {Resource} from "../resource.model";
import {StringUtils} from "../../../utils/string-utils.util";

export class RdbmsConnectionConfig implements Resource<RdbmsConnectionConfig> {
    isDefaultConnection: boolean = false;

    driverName: string;
    driverJar: string;
    driverClass: string;
    driverUrlPattern: string;

    host: string;
    port: number;

    useCustomUrl: boolean = false;
    customUrl: string;

    user: string;
    password: string;

    database: string;

    constructor() {
        this.reset();
    }

    reset(): void {
        this.isDefaultConnection = undefined;
        this.driverName = undefined;
        this.driverJar = undefined;
        this.driverClass = undefined;
        this.driverUrlPattern = undefined;
        this.host = undefined;
        this.port = undefined;
        this.useCustomUrl = undefined;
        this.customUrl = undefined;
        this.user = undefined;
        this.password = undefined;
        this.database = undefined;
    }

    isEmpty(): boolean {
        if(!StringUtils.isEmpty(this.driverName)) {return false;}
        if(!StringUtils.isEmpty(this.driverJar)) {return false;}
        if(!StringUtils.isEmpty(this.driverClass)) {return false;}
        if(!StringUtils.isEmpty(this.driverUrlPattern)) {return false;}
        if(!StringUtils.isEmpty(this.host)) {return false;}
        if(this.port) {return false;}
        if(!StringUtils.isEmpty(this.customUrl)) {return false;}
        if(!StringUtils.isEmpty(this.user)) {return false;}
        if(!StringUtils.isEmpty(this.password)) {return false;}
        if(!StringUtils.isEmpty(this.database)) {return false;}

        return true;
    }

    deserialize(input: Object): RdbmsConnectionConfig {
        this.driverName = input["driverName"];
        this.driverJar = input["driverJar"];
        this.driverClass = input["driverClass"];
        this.driverUrlPattern = input["driverUrlPattern"];

        this.isDefaultConnection = input["isDefaultConnection"];

        this.host = input["host"];
        this.port = input["port"];

        this.useCustomUrl = input["useCustomUrl"];
        this.customUrl = input["customUrl"];

        this.user = input["user"];
        this.password = input["password"];

        this.database = input["database"];
        return this;
    }

    serialize(): string {

        return  "" +
            '{' +
            '"driverName":' + JsonUtil.stringify(this.driverName) + ',' +
            '"driverJar":' + JsonUtil.stringify(this.driverJar) + ',' +
            '"driverClass":' + JsonUtil.stringify(this.driverClass) + ',' +
            '"driverUrlPattern":' + JsonUtil.stringify(this.driverUrlPattern) + ',' +

            '"isDefaultConnection":' + JsonUtil.stringify(this.isDefaultConnection) + ',' +

            '"host":' + JsonUtil.stringify(this.host) + ',' +
            '"port":' + JsonUtil.stringify(this.port) + ',' +

            '"useCustomUrl":' + JsonUtil.stringify(this.useCustomUrl) + ',' +
            '"customUrl":' + JsonUtil.stringify(this.customUrl) + ',' +

            '"user":' + JsonUtil.stringify(this.user) + ',' +
            '"password":' + JsonUtil.stringify(this.password) + ',' +

            '"database":' + JsonUtil.stringify(this.database) +
            '}'
    }

    clone(): RdbmsConnectionConfig {
        let objectAsJson = JSON.parse(this.serialize());
        return new RdbmsConnectionConfig().deserialize(objectAsJson);
    }

    createInstance(): RdbmsConnectionConfig {
        return new RdbmsConnectionConfig();
    }
}
