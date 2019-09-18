import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {StringTypeMeta} from "../../../../../model/text/parts/param-meta/string-type.meta";
import {JsonUtil} from "../../../../../utils/json.util";
import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {ObjectTreeModel} from "./interfaces/object-tree.model";
import {EnumTypeMeta} from "../../../../../model/text/parts/param-meta/enum-type.meta";
import {BooleanTypeMeta} from "../../../../../model/text/parts/param-meta/boolean-type.meta";
import {ListTypeMeta} from "../../../../../model/text/parts/param-meta/list-type.meta";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {MapTypeMeta} from "../../../../../model/text/parts/param-meta/map-type.meta";
import {MapItemObjectTreeModel} from "../nodes/map-node/item/map-item-object-tree.model";

export class MapObjectTreeModel extends JsonTreeContainerAbstract implements ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: MapTypeMeta;

    children: MapItemObjectTreeModel[] = [];

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: MapTypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;
    }

    serialize(): string {
        let result = "{";

        for (let i = 0; i < this.children.length; i++) {
            let child = this.children[i];
            result += child.serialize();

            if (i != this.children.length - 1) {
                result += ","
            }
        }

        result += "}";

        return result;
    }

    getChildren(): Array<JsonTreeNode> {
        return this.children;
    }
}
