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
import {DateTypeMeta} from "../../../../model/text/parts/param-meta/date-type-meta.model";
import {DateObjectTreeModel} from "./model/date-object-tree.model";
import {ListTypeMeta} from "../../../../model/text/parts/param-meta/list-type.meta";
import {ListObjectTreeModel} from "./model/list-object-tree.model";
import {MapTypeMeta} from "../../../../model/text/parts/param-meta/map-type.meta";
import {MapObjectTreeModel} from "./model/map-object-tree.model";
import {InstantTypeMeta} from "../../../../model/text/parts/param-meta/instant-type-meta.model";
import {LocalDateTimeTypeMeta} from "../../../../model/text/parts/param-meta/local-date-time-type-meta.model";
import {LocalDateTypeMeta} from "../../../../model/text/parts/param-meta/local-date-type-meta.model";
import {ZonedDateTimeTypeMeta} from "../../../../model/text/parts/param-meta/zoned-date-time-type-meta.model";
import {JsonTreeNode} from "../../../../generic/components/json-tree/model/json-tree-node.model";
import {MapItemObjectTreeModel} from "./nodes/map-node/item/map-item-object-tree.model";

@Injectable()
export class ObjectResourceComponentService {
    editMode: boolean = false;

    addFieldToObjectTree(parentContainer: JsonTreeContainer, serverType: TypeMeta, objectName: string, serverObject: object, isCondensedViewMode = false, isRootNode: boolean = false) {
        let childNode: JsonTreeNode;

        if (serverType instanceof BooleanTypeMeta) {
            childNode = new BooleanObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof DateTypeMeta) {
            childNode = new DateObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof EnumTypeMeta) {
            childNode = new EnumObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof InstantTypeMeta) {
            childNode = new DateObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof ListTypeMeta) {
            childNode = new ListObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            this.addListChildren(childNode as ListObjectTreeModel)
        }
        if (serverType instanceof LocalDateTimeTypeMeta) {
            childNode = new DateObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof LocalDateTypeMeta) {
            childNode = new DateObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof MapTypeMeta) {
            childNode = new MapObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            this.addMapChildren(childNode as MapObjectTreeModel);
        }
        if (serverType instanceof StringTypeMeta || serverType instanceof NumberTypeMeta) {
            childNode = new StringObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }
        if (serverType instanceof ObjectTypeMeta) {
            childNode = new ObjectObjectTreeModel(parentContainer, objectName, serverObject, serverType);
            this.addObjectChildren(childNode as ObjectObjectTreeModel);
        }
        if (serverType instanceof ZonedDateTimeTypeMeta) {
            childNode = new DateObjectTreeModel(parentContainer, objectName, serverObject, serverType)
        }

        if (isCondensedViewMode && isRootNode && childNode && childNode.isContainer()) {
            for (const childOfChildNode of (childNode as JsonTreeContainer).getChildren()) {
                parentContainer.getChildren().push(childOfChildNode)
            }
        } else {
            parentContainer.getChildren().push(childNode);
        }
    }

    private addObjectChildren(model: ObjectObjectTreeModel) {
        let objectTypeMeta = model.typeMeta as ObjectTypeMeta;

        for (const field of objectTypeMeta.fields) {
            let fieldValue = model.serverObject ? model.serverObject[field.name] : null;
            this.addFieldToObjectTree(
                model,
                field.type,
                field.name,
                fieldValue
            )
        }
    }

    private addListChildren(model: ListObjectTreeModel) {
        let listTypeMeta = model.typeMeta as ListTypeMeta;

        if (model.serverObject) {
            for (let i = 0; i < model.serverObject.length; i++) {
                const item = model.serverObject[i];
                this.addFieldToObjectTree(
                    model,
                    listTypeMeta.itemsType,
                    "item " + i,
                    item
                )
            }
        }
    }

    private addMapChildren(model: MapObjectTreeModel) {
        let typeMeta = model.typeMeta;

        if (model.serverObject) {
            let index = 0;
            for (var prop in model.serverObject) {
                if (Object.prototype.hasOwnProperty.call(model.serverObject, prop)) {
                    let mapItemObjectTreeModel = new MapItemObjectTreeModel(
                        model,
                        index,
                        prop,
                        typeMeta.keyType,
                        model.serverObject[prop],
                        typeMeta.valueType
                    );
                    model.children.push(mapItemObjectTreeModel);
                    this.addMapItemChildren(mapItemObjectTreeModel);
                    index++;
                }
            }
        }
    }

    private addMapItemChildren(model: MapItemObjectTreeModel) {
        this.addFieldToObjectTree(
            model,
            model.keyTypeMeta,
            "key",
            model.keyObject
        );
        this.addFieldToObjectTree(
            model,
            model.valueTypeMeta,
            "value",
            model.valueObject
        );
    }
}
