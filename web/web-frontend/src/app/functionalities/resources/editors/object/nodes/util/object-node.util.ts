import {TypeMeta} from "../../../../../../model/text/parts/param-meta/type-meta.model";
import {StringTypeMeta} from "../../../../../../model/text/parts/param-meta/string-type.meta";
import {NumberTypeMeta} from "../../../../../../model/text/parts/param-meta/number-type.meta";
import {BooleanTypeMeta} from "../../../../../../model/text/parts/param-meta/boolean-type.meta";
import {DateTypeMeta} from "../../../../../../model/text/parts/param-meta/date-type-meta.model";
import {EnumTypeMeta} from "../../../../../../model/text/parts/param-meta/enum-type.meta";
import {ListTypeMeta} from "../../../../../../model/text/parts/param-meta/list-type.meta";
import {MapTypeMeta} from "../../../../../../model/text/parts/param-meta/map-type.meta";
import {ObjectTypeMeta} from "../../../../../../model/text/parts/param-meta/object-type.meta";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {ObjectObjectTreeModel} from "../../model/object-object-tree.model";
import {ObjectTreeModel} from "../../model/interfaces/object-tree.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {ArrayUtil} from "../../../../../../utils/array.util";

export class ObjectNodeUtil {

    static getFieldTypeForUI(typeMeta: TypeMeta): string {
        if (typeMeta instanceof BooleanTypeMeta) {
            return "Boolean"
        }
        if (typeMeta instanceof DateTypeMeta) {
            return "Date"
        }
        if (typeMeta instanceof EnumTypeMeta) {
            return "Enum"
        }
        if (typeMeta instanceof ListTypeMeta) {
            return "List"
        }
        if (typeMeta instanceof MapTypeMeta) {
            return "Map"
        }
        if (typeMeta instanceof StringTypeMeta) {
            return "String"
        }
        if (typeMeta instanceof NumberTypeMeta) {
            return "Number"
        }
        if (typeMeta instanceof ObjectTypeMeta) {
            return StringUtils.substringAfterLast(typeMeta.javaType, ".")
        }
        return "Object"
    }

    static getNodeNameForUI(model: ObjectTreeModel): string {
        if(model.getParent() instanceof ListObjectTreeModel) {
            let itemIndex = (model.getParent() as JsonTreeContainer).getChildren().indexOf(model, 0);
            return "item " + itemIndex
        }

        return model.objectName;
    }
}
