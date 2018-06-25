import {StringUtils} from "../../../utils/string-utils.util";

export class HttpRequestBodyTypeForm implements Serializable<HttpRequestBodyTypeForm> {
    key: string;
    value: string;

    deserialize(input: Object): HttpRequestBodyTypeForm {
        let formLine = input as string;

        this.key = decodeURIComponent (StringUtils.substringBefore(formLine, "="));
        this.value = decodeURIComponent (StringUtils.substringAfter(formLine, "="));

        return this;
    }

    serialize(): string {
        let key = this.key ? encodeURIComponent(this.key) : "";
        let value = this.value ? encodeURIComponent(this.value): "";
        return key +"="+ value;
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
