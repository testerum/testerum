
import {JsonUtil} from "../../../../../../../utils/json.util";
import {Resource} from "../../../../../../../model/resource/resource.model";
import {StringUtils} from "../../../../../../../utils/string-utils.util";

export class HttpMockServer implements Resource<HttpMockServer> {

    port: string;

    constructor() {
        this.reset();
    }

    reset(): void {
        this.port = undefined;
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(!StringUtils.isEmpty(this.port)) {isEmpty = false;}

        return isEmpty;
    }

    deserialize(input: Object): HttpMockServer {
        this.port = input["port"];

        return this;
    }

    serialize(): string {
        let hasSavedFields = false;
        let result = '{';
        if (this.port) {
            hasSavedFields = true;
            result += '"port":' + JsonUtil.stringify(this.port);
        }

        result += '}';
        return result;
    }

    clone(): HttpMockServer {
        let objectAsJson = JSON.parse(this.serialize());
        return new HttpMockServer().deserialize(objectAsJson);
    }

    createInstance(): HttpMockServer {
        return new HttpMockServer();
    }
}
