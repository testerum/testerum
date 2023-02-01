import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ObjectTreeModel} from "./interfaces/object-tree.model";
import {ListTypeMeta} from "../../../../../model/text/parts/param-meta/list-type.meta";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";

export class ListObjectTreeModel extends JsonTreeContainerAbstract implements ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: ListTypeMeta;

    children: ObjectTreeModel[] = [];

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: ListTypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;
    }

    isEmpty(): boolean {
        return this.children.length == 0;
    }

    serialize(): string {
        let result = "[";

        for (let i = 0; i < this.children.length; i++) {
            let child = this.children[i];
            result += child.serialize();

            if (i < this.children.length - 1) {
                result += ","
            }
        }

        result += "]";

        return result;
    }

    getChildren(): Array<JsonTreeNode> {
        return this.children;
    }
}
