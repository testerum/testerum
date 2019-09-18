import {TypeMeta} from "../../../../../../../model/text/parts/param-meta/type-meta.model";
import {JsonTreeContainerAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {ObjectTreeModel} from "../../../model/interfaces/object-tree.model";

export class MapItemObjectTreeModel extends JsonTreeContainerAbstract {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    indexInParent: number;

    keyObject: any;
    keyTypeMeta: TypeMeta;

    valueObject: any;
    valueTypeMeta: TypeMeta;

    children: ObjectTreeModel[] = [];

    constructor(parentContainer: JsonTreeContainer, indexInParent: number, keyObject: any, keyTypeMeta: TypeMeta, valueObject: any, valueTypeMeta: TypeMeta) {
        super(parentContainer);
        this.indexInParent = indexInParent;
        this.keyObject = keyObject;
        this.keyTypeMeta = keyTypeMeta;
        this.valueObject = valueObject;
        this.valueTypeMeta = valueTypeMeta;
    }

    serialize(): string {
        let result = "";
        result += this.getChildByType(true).serialize();
        result += ":";
        result += this.getChildByType(false).serialize();
        return result;
    }

    private getChildByType(getKeyChild: boolean) {
        for (const child of this.children) {
            if (getKeyChild && child.objectName == "key") {
                return child
            }
            if (!getKeyChild && child.objectName == "value") {
                return child
            }
        }
        return null
    }

    getChildren(): Array<JsonTreeNode> {
        return this.children;
    }
}
