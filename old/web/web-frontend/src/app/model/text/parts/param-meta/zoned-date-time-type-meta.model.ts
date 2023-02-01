import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class ZonedDateTimeTypeMeta implements TypeMeta, Serializable<ZonedDateTimeTypeMeta> {
    javaType: string;

    constructor(javaType = "java.time.ZonedDateTime") {
        this.javaType = javaType;
    }

    deserialize(input: Object): ZonedDateTimeTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "ZONED_DATE_TIME"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            '}'
    }
}
