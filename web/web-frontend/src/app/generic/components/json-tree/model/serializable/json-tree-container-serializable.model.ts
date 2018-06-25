

import {JsonTreeContainer} from "../json-tree-container.model";
import {JsonTreeNode} from "../json-tree-node.model";
import {JsonTreeNodeSerializable} from "./json-tree-node-serialzable.model";
import {JsonTreeNodeState} from "../json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../behavior/JsonTreeContainerOptions";

export abstract class JsonTreeContainerSerializable extends JsonTreeNodeSerializable implements JsonTreeContainer, Serializable<any>{

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    abstract getChildren(): Array<JsonTreeNodeSerializable>;

    isContainer(): boolean {
        return true;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }

    abstract deserialize(input: Object): any;
    abstract serialize(): string;

}
