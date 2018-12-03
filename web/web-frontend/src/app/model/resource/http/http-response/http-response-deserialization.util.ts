import {HttpResponse} from "./http-response.model";
import {HttpResponseType} from "./http-response-type.enum";
import {ValidHttpResponse} from "./valid-http-response.model";
import {InvalidHttpResponse} from "./invalid-http-response.model";

export class HttpResponseDeserializationUtil {

    static deserialize(object: Object): HttpResponse {
        if (object["@type"] == HttpResponseType[HttpResponseType.VALID]) {
            return new ValidHttpResponse().deserialize(object);
        }
        if (object["@type"] == HttpResponseType[HttpResponseType.INVALID]) {
            return new InvalidHttpResponse().deserialize(object);
        }

        throw Error(`unknown http response type [${object["@type"]}]`);

    }

}
