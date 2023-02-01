import {StringUtils} from "../../../../utils/string-utils.util";
import {HttpResponse} from "./http-response.model";
import {JsonUtil} from "../../../../utils/json.util";
import {HttpResponseType} from "./http-response-type.enum";
import {Resource} from "../../resource.model";

export class InvalidHttpResponse implements HttpResponse, Resource<InvalidHttpResponse> {

    errorMessage: string;

    deserialize(input: Object): InvalidHttpResponse {
        this.errorMessage = input["errorMessage"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type":' + HttpResponseType[HttpResponseType.INVALID] +
            ',"errorMessage":' + JsonUtil.stringify(this.errorMessage) +
            '}'
    }

    clone(): InvalidHttpResponse {
        let objectAsJson = JSON.parse(this.serialize());
        return new InvalidHttpResponse().deserialize(objectAsJson);
    }

    createInstance(): InvalidHttpResponse {
        return new InvalidHttpResponse();
    }

    isEmpty(): boolean {
        if (!StringUtils.isEmpty(this.errorMessage)) { return false; }

        return true;
    }

    reset(): void {
        this.errorMessage = undefined;
    }

}
