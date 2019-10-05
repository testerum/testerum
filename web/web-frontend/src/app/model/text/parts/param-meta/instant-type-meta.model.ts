import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class InstantTypeMeta implements TypeMeta, Serializable<InstantTypeMeta> {
    javaType: string;

    constructor(javaType = "java.time.Instant") {
        this.javaType = javaType;
    }

    deserialize(input: Object): InstantTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "INSTANT"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            '}'
    }
}
