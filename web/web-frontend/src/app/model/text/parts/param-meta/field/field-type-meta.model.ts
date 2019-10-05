import {Serializable} from "../../../../infrastructure/serializable.model";
import {TypeMeta} from "../type-meta.model";
import {BooleanTypeMeta} from "../boolean-type.meta";
import {DateTypeMeta} from "../date-type-meta.model";
import {NumberTypeMeta} from "../number-type.meta";
import {ListTypeMeta} from "../list-type.meta";
import {JsonUtil} from "../../../../../utils/json.util";
import {ObjectTypeMeta} from "../object-type.meta";
import {StringTypeMeta} from "../string-type.meta";
import {MapTypeMeta} from "../map-type.meta";
import {EnumTypeMeta} from "../enum-type.meta";
import {InstantTypeMeta} from "../instant-type-meta.model";
import {LocalDateTimeTypeMeta} from "../local-date-time-type-meta.model";
import {LocalDateTypeMeta} from "../local-date-type-meta.model";
import {ZonedDateTimeTypeMeta} from "../zoned-date-time-type-meta.model";

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
            if (itemsTypeSerialized["@type"] == "ENUM") {
                return new EnumTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "INSTANT") {
                return new InstantTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "LIST") {
                return new ListTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "LOCAL_DATE_TIME") {
                return new LocalDateTimeTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "LOCAL_DATE") {
                return new LocalDateTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "MAP") {
                return new MapTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "NUMBER") {
                return new NumberTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "OBJECT") {
                return new ObjectTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "TEXT") {
                return new StringTypeMeta().deserialize(itemsTypeSerialized);
            }
            if (itemsTypeSerialized["@type"] == "ZONED_DATE_TIME") {
                return new ZonedDateTimeTypeMeta().deserialize(itemsTypeSerialized);
            }
        }
        return null;
    }
}
