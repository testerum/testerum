import {JsonTreeNode} from "../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {StringTypeMeta} from "../../../../../../model/text/parts/param-meta/string-type.meta";


export interface ObjectTreeModel extends JsonTreeNode {

    objectName: string;
    serverObject: any;
    typeMeta: StringTypeMeta;

    serialize(): string;
}
