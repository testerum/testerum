
import {HttpMockResponseBodyType} from "../enums/http-mock-response-body-type.enum";
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockResponseBody implements Serializable<HttpMockResponseBody> {

    bodyType: HttpMockResponseBodyType = HttpMockResponseBodyType.OTHER;
    content: string;

    constructor() {
        this.reset();
    }

    reset() {
        this.bodyType = HttpMockResponseBodyType.OTHER;
        this.content = undefined;
    }

    isEmpty(): boolean {
        return this.content == null
    }

    deserialize(input: Object): HttpMockResponseBody {

        if (input['bodyType']) {
            this.bodyType = HttpMockResponseBodyType.fromSerialized(input['bodyType']);
        }

        if (input['content']) {
            this.content = input['content'];
        }

        return this;
    }

    serialize(): string {
        let shouldAddComa = false;

        let result = '{';

        if (this.bodyType) {
            if(shouldAddComa) {
                result += ',';
            }
            shouldAddComa = true;

            result += '"bodyType":' + JsonUtil.stringify(this.bodyType.serialized);
        }

        if (this.content) {
            if(shouldAddComa) {
                result += ',';
            }
            shouldAddComa = true;

            result += '"content":' + JsonUtil.stringify(this.content);
        }

        result += '}';

        return result;
    }
}
