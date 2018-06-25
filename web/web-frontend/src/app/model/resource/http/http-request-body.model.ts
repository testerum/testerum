
import {HttpRequestBodyType} from "./enum/http-request-body-type.enum";
import {HttpRequestBodyTypeForm} from "./http-request-body-type-form.model";
import {JsonUtil} from "../../../utils/json.util";
import {StringUtils} from "../../../utils/string-utils.util";
import {HttpMethod} from "../../enums/http-method-enum";

export class HttpRequestBody implements Serializable<HttpRequestBody> {
    bodyType: HttpRequestBodyType = HttpRequestBodyType.RAW;
    content: string;
    contentAsForm:Array<HttpRequestBodyTypeForm> = [];

    constructor() {
        this.reset();
    }

    reset() {
        this.bodyType = HttpRequestBodyType.RAW;
        this.content = null;
        this.contentAsForm.length = 0;
    }

    isEmpty(): boolean {
        if(this.bodyType == HttpRequestBodyType.RAW) {
            return this.content == null;
        } else {
            return this.contentAsForm.length == 0;
        }
    }

    deserialize(input: Object): HttpRequestBody {
        this.bodyType = HttpRequestBodyType.fromString(input["bodyType"]);

        if(this.bodyType == HttpRequestBodyType.RAW) {
            this.content = input["content"];
        } else {
            if(input['content']){
                this.deserializeContentTypeForm(input["content"] as string);
            } else {
                this.contentAsForm.push(new HttpRequestBodyTypeForm())
            }
        }
        return this;
    }

    private deserializeContentTypeForm(content: string) {
        let formLines = content.split('&');

        for (let formLine of formLines) {
            if(StringUtils.isEmpty(formLine)) {
                continue;
            }
            this.contentAsForm.push(new HttpRequestBodyTypeForm().deserialize(formLine));
        }
        this.contentAsForm.push(new HttpRequestBodyTypeForm())
    }

    serialize(): string {
        let result = '' +
            '{' +
            '"bodyType":' + JsonUtil.stringify(this.bodyType.toString().toUpperCase());
        if(this.bodyType == HttpRequestBodyType.RAW) {
            result += ',"content":'+JsonUtil.stringify(this.content)
        }
        if (this.bodyType == HttpRequestBodyType.X_WWW_FORM_URLENCODED){
            let contentAsFormWithValue = this.getContentAsFormWithValue();
            if(contentAsFormWithValue.length != 0 ) {
                result += ',"content":' + JsonUtil.stringify(this.serializeContentTypeForm());
            }
        }

        result += '}';
        return result;
    }

    private getContentAsFormWithValue(): Array<HttpRequestBodyTypeForm> {
        let result:Array<HttpRequestBodyTypeForm> = [];
        for (let bodyFormElement of this.contentAsForm) {
            if(!bodyFormElement.isEmpty()) {
                result.push(bodyFormElement)
            }
        }

        return result;
    }

    private serializeContentTypeForm(): string {
        let result = '';

        let isFirstParam = true;
        for (let formEntry of this.contentAsForm) {
            if(formEntry.isEmpty()) {
                continue
            }

            if(isFirstParam) {
                isFirstParam = false;
            } else {
                result += "&"
            }
            result += formEntry.serialize();
        }
        return result;
    }
}
