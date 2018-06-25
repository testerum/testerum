
import {JsonTreeNode} from "../json-tree-node.model";
import {JsonTreeContainer} from "../json-tree-container.model";

export abstract class JsonTreeNodeSerializable implements JsonTreeNode, Serializable<any> {

    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer) {
        this.parentContainer = parentContainer;
    }

    getParent(): JsonTreeContainer {
        return this.parentContainer;
    }

    isContainer(): boolean {
        return false;
    }

    abstract deserialize(input: Object): any;
    abstract serialize(): string;
}
