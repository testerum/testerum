import {Serializable} from "../../../../infrastructure/serializable.model";
import {TypeMeta} from "../type-meta.model";
import {BooleanTypeMeta} from "../boolean-type.meta";
import {DateTypeMeta} from "../date-type-meta.model";
import {NumberTypeMeta} from "../number-type.meta";
import {ListTypeMeta} from "../list-type.meta";
import {JsonUtil} from "../../../../../utils/json.util";
import {ObjectTypeMeta} from "../object-type.meta";
import {TextTypeMeta} from "../text-type.meta";

export class FieldTypeMeta implements Serializable<FieldTypeMeta> {
    name: string;
    type: TypeMeta;

    deserialize(input: Object): FieldTypeMeta {
        this.name = input["name"];
        this.type = FieldTypeMeta.deserializeTypeMeta(input["type"]);
        return this;
    }

    serialize(): string {
        return '' +
        '{' +
        '"name":' + JsonUtil.stringify(this.name) +
        '"type":' + this.type ? this.type.serialize() : null +
        '}'
    }

    static deserializeTypeMeta(itemsTypeSerialized: any): TypeMeta {
        if (itemsTypeSerialized) {
            if (itemsTypeSerialized["@type"] == "BOOLEAN") {
                return new BooleanTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "DATE") {
                return new DateTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "LIST") {
                return new ListTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "NUMBER") {
                return new NumberTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "OBJECT") {
                return new ObjectTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "TEXT") {
                return new TextTypeMeta().deserialize(itemsTypeSerialized);
            }
        }
        return null;
    }
}
