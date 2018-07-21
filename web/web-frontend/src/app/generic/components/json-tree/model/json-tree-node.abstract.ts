
import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNode} from "./json-tree-node.model";

export abstract class JsonTreeNodeAbstract implements JsonTreeNode {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    constructor(parentContainer: JsonTreeContainer) {
        this.parentContainer = parentContainer;
    }

    getParent(): JsonTreeContainer {
        return this.parentContainer;
    }

    isContainer(): boolean {
        return false;
    }

    isHidden(): boolean {
        return this.hidden;
    }

    setHidden(isHidden: boolean) {
        this.hidden = isHidden;
    }
}
