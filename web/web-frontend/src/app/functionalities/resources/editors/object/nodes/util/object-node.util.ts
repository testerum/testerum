import {TypeMeta} from "../../../../../../model/text/parts/param-meta/type-meta.model";
import {StringTypeMeta} from "../../../../../../model/text/parts/param-meta/string-type.meta";
import {NumberTypeMeta} from "../../../../../../model/text/parts/param-meta/number-type.meta";
import {BooleanTypeMeta} from "../../../../../../model/text/parts/param-meta/boolean-type.meta";
import {DateTypeMeta} from "../../../../../../model/text/parts/param-meta/date-type-meta.model";
import {EnumTypeMeta} from "../../../../../../model/text/parts/param-meta/enum-type.meta";
import {ListTypeMeta} from "../../../../../../model/text/parts/param-meta/list-type.meta";
import {MapTypeMeta} from "../../../../../../model/text/parts/param-meta/map-type.meta";

export class ObjectNodeUtil {

    static getFieldTypeForUI(typeMeta: TypeMeta): string {
        if (typeMeta instanceof BooleanTypeMeta) {
            return "boolean"
        }
        if (typeMeta instanceof DateTypeMeta) {
            return "date"
        }
        if (typeMeta instanceof EnumTypeMeta) {
            return "enum"
        }
        if (typeMeta instanceof ListTypeMeta) {
            return "list"
        }
        if (typeMeta instanceof MapTypeMeta) {
            return "map"
        }
        if (typeMeta instanceof StringTypeMeta) {
            return "string"
        }
        if (typeMeta instanceof NumberTypeMeta) {
            return "number"
        }
        return "object"
    }
}
