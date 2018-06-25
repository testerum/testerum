
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeContainerAbstract} from "./json-tree-container.abstract";

export class JsonTreeModel extends JsonTreeContainerAbstract {

    children:Array<JsonTreeNode> = [];

    constructor() {
        super(null);
    }

    getChildren(): Array<JsonTreeNode> {
        return this.children;
    }
}
