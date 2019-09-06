import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class DateTypeMeta implements TypeMeta, Serializable<DateTypeMeta> {
    javaType: string;

    constructor(javaType = "java.util.Date") {
        this.javaType = javaType;
    }

    deserialize(input: Object): DateTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "DATE"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            '}'
    }
}
