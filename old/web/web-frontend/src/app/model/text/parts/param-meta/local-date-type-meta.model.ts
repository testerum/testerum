import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class LocalDateTypeMeta implements TypeMeta, Serializable<LocalDateTypeMeta> {
    javaType: string;

    constructor(javaType = "java.time.LocalDate") {
        this.javaType = javaType;
    }

    deserialize(input: Object): LocalDateTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "LOCAL_DATE"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            '}'
    }
}
