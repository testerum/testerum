import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {FieldTypeMeta} from "./field/field-type-meta.model";
import {TextTypeMeta} from "./text-type.meta";

export class ListTypeMeta implements TypeMeta, Serializable<ListTypeMeta> {
    javaType: string;
    itemsType: TypeMeta;

    constructor(javaType = "java.util.ArrayList", itemsType = new TextTypeMeta()) {
        this.javaType = javaType;
        this.itemsType = itemsType;
    }

    deserialize(input: Object): ListTypeMeta {
        this.javaType = input["javaType"];

        let itemsTypeSerialized = input["itemsType"];
        this.itemsType = FieldTypeMeta.deserializeTypeMeta(itemsTypeSerialized);

        return this;
    }

    serialize(): string {
        return ''+
            '{' +
            '"@type": "LIST"'+
            ',"javaType":'+ JsonUtil.stringify(this.javaType) +
            ',"itemsType":'+ this.itemsType ? this.itemsType.serialize() : null +
            '}'
    }
}
