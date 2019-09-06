import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";

export class EnumTypeMeta implements TypeMeta, Serializable<EnumTypeMeta> {
    javaType: string;
    possibleValues: string[] = [];

    constructor(javaType = "ENUM") {
        this.javaType = javaType;
    }

    deserialize(input: Object): EnumTypeMeta {
        this.javaType = input["javaType"];

        this.possibleValues = [];
        for (let possibleValue of (input['possibleValues'] || [])) {
            this.possibleValues.push( possibleValue );
        }
        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "ENUM"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            ',"possibleValues":'+ JsonUtil.serializeArray(this.possibleValues) +
            '}'
    }
}
