import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class BooleanTypeMeta implements TypeMeta, Serializable<BooleanTypeMeta> {
    javaType: string;

    constructor(javaType = "java.lang.Boolean") {
        this.javaType = javaType;
    }

    deserialize(input: Object): BooleanTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
               '{' +
               '"@type": "BOOLEAN"'+
               ',"javaType":'+ JsonUtil.stringify(this.javaType) +
               '}'
    }
}
