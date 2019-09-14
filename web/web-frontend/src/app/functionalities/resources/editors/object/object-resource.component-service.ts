import {Injectable} from "@angular/core";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {TypeMeta} from "../../../../model/text/parts/param-meta/type-meta.model";
import {StringTypeMeta} from "../../../../model/text/parts/param-meta/string-type.meta";
import {NumberTypeMeta} from "../../../../model/text/parts/param-meta/number-type.meta";
import {StringObjectTreeModel} from "./model/string-object-tree.model";
import {ObjectTypeMeta} from "../../../../model/text/parts/param-meta/object-type.meta";
import {ObjectObjectTreeModel} from "./model/object-object-tree.model";
import {EnumTypeMeta} from "../../../../model/text/parts/param-meta/enum-type.meta";
import {EnumObjectTreeModel} from "./model/enum-object-tree.model";
import {BooleanTypeMeta} from "../../../../model/text/parts/param-meta/boolean-type.meta";
import {BooleanObjectTreeModel} from "./model/boolean-object-tree.model";

@Injectable()
export class ObjectResourceComponentService {
    editMode: boolean = false;

    addFieldToObjectTree(parentContainer: JsonTreeContainer, serverType: TypeMeta, objectName: string, serverObject: object) {
        if (serverType instanceof BooleanTypeMeta) {
            parentContainer.getChildren().push(
                new BooleanObjectTreeModel(parentContainer, objectName, serverObject, serverType)
            )
        }
        if (serverType instanceof EnumTypeMeta) {
            parentContainer.getChildren().push(
                new EnumObjectTreeModel(parentContainer, objectName, serverObject, serverType)
            )
        }
        if (serverType instanceof StringTypeMeta || serverType instanceof NumberTypeMeta) {
            parentContainer.getChildren().push(
                new StringObjectTreeModel(parentContainer, objectName, serverObject, serverType)
            )
        }
        if (serverType instanceof ObjectTypeMeta) {
            parentContainer.getChildren().push(
                new ObjectObjectTreeModel(parentContainer, objectName, serverObject, serverType)
            )
        }
    }
}
