import {JsonTreeNode} from "../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {TextTypeMeta} from "../../../../../../model/text/parts/param-meta/text-type.meta";


export interface ObjectTreeModel extends JsonTreeNode {

    objectName: string;
    serverObject: any;
    typeMeta: TextTypeMeta;

    serialize(): string;
}
