
import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeNodeState} from "./json-tree-node-state.model";
import {JsonTreeContainerOptions} from "./behavior/JsonTreeContainerOptions";

export abstract class JsonTreeContainerAbstract implements JsonTreeContainer{

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

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

    isHidden(): boolean {
        return this.hidden;
    }

    setHidden(isHidden: boolean) {
        this.hidden = isHidden;
    }

    isSelected(): boolean {
        return this.selected;
    }

    setSelected(isSelected: boolean) {
        this.selected = isSelected;
    }
}
