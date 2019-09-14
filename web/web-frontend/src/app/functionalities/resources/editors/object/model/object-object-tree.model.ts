import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {StringTypeMeta} from "../../../../../model/text/parts/param-meta/string-type.meta";
import {JsonUtil} from "../../../../../utils/json.util";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {ObjectTreeModel} from "./interfaces/object-tree.model";

export class ObjectObjectTreeModel extends JsonTreeContainerAbstract implements ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: StringTypeMeta;

    children: ObjectTreeModel[] = [];

    value: object;

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: StringTypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;

        this.value = serverObject;
    }

    serialize(): string {
        let result = "{";

        for (let i = 0; i < this.children.length; i++) {
            let child = this.children[i];
            result += JsonUtil.stringify(child.objectName) +": "+child.serialize();

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
