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

@Injectable()
export class ObjectResourceComponentService {
    editMode: boolean = false;

    addFieldToObjectTree(parentContainer: JsonTreeContainer, serverType: TypeMeta, objectName: string, serverObject: object) {
        if (serverType instanceof EnumTypeMeta) {
            let enumObjectTreeModel = new EnumObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            parentContainer.getChildren().push(enumObjectTreeModel)
        }
        if (serverType instanceof StringTypeMeta || serverType instanceof NumberTypeMeta) {
            let stringObjectTreeModel = new StringObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            parentContainer.getChildren().push(stringObjectTreeModel)
        }
        if (serverType instanceof ObjectTypeMeta) {
            let objectTreeModel = new ObjectObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            parentContainer.getChildren().push(objectTreeModel)
        }
    }
}
