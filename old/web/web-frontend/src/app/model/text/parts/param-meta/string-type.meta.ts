import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class StringTypeMeta implements TypeMeta, Serializable<StringTypeMeta> {
    javaType: string;

    constructor(javaType = "java.lang.String") {
        this.javaType = javaType;
    }

    deserialize(input: Object): StringTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
               '{' +
               '"@type": "TEXT"'+
               ',"javaType":'+ JsonUtil.stringify(this.javaType) +
               '}'
    }
}
