import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {TypeMetaFieldDescriptor} from "./field/field-type-meta.model";
import {StringTypeMeta} from "./string-type.meta";

export class MapTypeMeta implements TypeMeta, Serializable<MapTypeMeta> {
    javaType: string;
    keyType: TypeMeta;
    valueType: TypeMeta;

    constructor(javaType = "java.util.HashMap", keyType = new StringTypeMeta(), valueType = new StringTypeMeta()) {
        this.javaType = javaType;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    deserialize(input: Object): MapTypeMeta {
        this.javaType = input["javaType"];

        let keyTypeSerialized = input["keyType"];
        this.keyType = TypeMetaFieldDescriptor.deserializeTypeMeta(keyTypeSerialized);

        let valueTypeSerialized = input["valueType"];
        this.valueType = TypeMetaFieldDescriptor.deserializeTypeMeta(valueTypeSerialized);

        return this;
    }

    serialize(): string {
        return ''+
        '{' +
        '"@type": "MAP"'+
        ',"javaType":'+ JsonUtil.stringify(this.javaType) +
        ',"keyType":'+ JsonUtil.serializeSerializable(this.keyType) +
        ',"valueType":'+ JsonUtil.serializeSerializable(this.valueType) +
        '}'
    }
}
