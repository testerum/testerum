import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {TypeMetaFieldDescriptor} from "./field/field-type-meta.model";
import {StringTypeMeta} from "./string-type.meta";

export class ListTypeMeta implements TypeMeta, Serializable<ListTypeMeta> {
    javaType: string;
    itemsType: TypeMeta;

    constructor(javaType = "java.util.ArrayList", itemsType = new StringTypeMeta()) {
        this.javaType = javaType;
        this.itemsType = itemsType;
    }

    deserialize(input: Object): ListTypeMeta {
        this.javaType = input["javaType"];

        let itemsTypeSerialized = input["itemsType"];
        this.itemsType = TypeMetaFieldDescriptor.deserializeTypeMeta(itemsTypeSerialized);

        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "LIST"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            ',"itemsType":'+ JsonUtil.serializeSerializable(this.itemsType) +
            '}'
    }
}
