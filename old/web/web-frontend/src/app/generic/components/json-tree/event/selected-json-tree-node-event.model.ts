
import {TreeNodeModel} from "../../../../model/infrastructure/tree-node.model";
import {JsonTreeModel} from "../model/json-tree.model";
import {JsonTreeNode} from "../model/json-tree-node.model";

export class JsonTreeNodeEventModel {
    treeNode:JsonTreeNode;

    constructor(treeNode:JsonTreeNode) {
        this.treeNode = treeNode;
    }
}
