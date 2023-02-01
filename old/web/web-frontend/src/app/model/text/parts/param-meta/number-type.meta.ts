import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class NumberTypeMeta implements TypeMeta, Serializable<NumberTypeMeta> {
    javaType: string;

    constructor(javaType = "java.lang.Double") {
        this.javaType = javaType;
    }

    deserialize(input: Object): NumberTypeMeta {
        this.javaType = input["javaType"];
        return this;
    }

    serialize(): string {
        return ''+
               '{' +
               '"@type": "NUMBER"'+
               ',"javaType":'+ JsonUtil.stringify(this.javaType) +
               '}'
    }
}
