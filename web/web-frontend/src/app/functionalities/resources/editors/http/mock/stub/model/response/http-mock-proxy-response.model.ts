
import {Enum} from "../../../../../../../../model/enums/enum.interface";
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockProxyResponse implements Serializable<HttpMockProxyResponse>{

    proxyBaseUrl: string;

    constructor() {
        this.reset();
    }

    reset(): void {
        this.proxyBaseUrl = undefined;
    }

    deserialize(input: Object): HttpMockProxyResponse {
        if (input['proxyBaseUrl']) {
            this.proxyBaseUrl = input['proxyBaseUrl'];
        }
        return this;
    }

    serialize(): string {
        let response = '{';

        if(this.proxyBaseUrl) {
            response += '"proxyBaseUrl":' + JsonUtil.stringify(this.proxyBaseUrl);
        }
        response += '}';

        return response;
    }
}
