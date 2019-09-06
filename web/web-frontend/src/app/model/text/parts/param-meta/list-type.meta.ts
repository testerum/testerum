import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {FieldTypeMeta} from "./field/field-type-meta.model";

export class ListTypeMeta implements TypeMeta, Serializable<ListTypeMeta> {
    javaType: string;
    itemsType: TypeMeta;

    constructor(javaType = "java.util.ArrayList") {
        this.javaType = javaType;
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
