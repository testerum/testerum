import {TypeMeta} from "./type-meta.model";
import {Serializable} from "../../../infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {TypeMetaFieldDescriptor} from "./field/field-type-meta.model";

export class ObjectTypeMeta implements TypeMeta, Serializable<ObjectTypeMeta> {
    javaType: string;
    fields: TypeMetaFieldDescriptor[] = [];

    constructor(javaType = null) {
        this.javaType = javaType;
    }

    getFieldTypeMetaByName(fieldName: string): TypeMetaFieldDescriptor | null {
        for (const field of this.fields) {
            if (field.name == fieldName) {
                return field
            }
        }
        return null;
    }

    deserialize(input: Object): ObjectTypeMeta {
        this.javaType = input["javaType"];

        if (input["fields"]) {
            for (const field of input["fields"] || []) {
                this.fields.push(
                    new TypeMetaFieldDescriptor().deserialize(field)
                )
            }
        }
        return this;
    }

    serialize(): string {
        return '' +
            '{' +
            '"@type": "OBJECT"' +
            ',"javaType":' + JsonUtil.stringify(this.javaType) +
            ',"fields":' + JsonUtil.serializeArrayOfSerializable(this.fields) +
            '}'
    }
}
