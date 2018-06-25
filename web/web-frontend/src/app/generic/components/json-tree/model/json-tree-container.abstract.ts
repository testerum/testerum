
import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeNodeState} from "./json-tree-node-state.model";
import {JsonTreeContainerOptions} from "./behavior/JsonTreeContainerOptions";

export abstract class JsonTreeContainerAbstract implements JsonTreeContainer{

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    private _options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer) {
        this.parentContainer = parentContainer;
    }

    abstract getChildren(): Array<JsonTreeNode>;

    getParent(): JsonTreeContainer {
        return this.parentContainer;
    }

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
        return this._options;
    }
}
