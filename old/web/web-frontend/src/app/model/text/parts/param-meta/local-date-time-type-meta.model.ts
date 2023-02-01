import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class LocalDateTimeTypeMeta implements TypeMeta, Serializable<LocalDateTimeTypeMeta> {
    javaType: string;

    constructor(javaType = "java.time.LocalDateTime") {
        this.javaType = javaType;
    }

    deserialize(input: Object): LocalDateTimeTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "LOCAL_DATE_TIME"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            '}'
    }
}
