
import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeNodeOptions} from "./behavior/JsonTreeNodeOptions";
import {JsonTreeContainerOptions} from "./behavior/JsonTreeContainerOptions";

export abstract class JsonTreeNodeAbstract implements JsonTreeNode {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;
    private _options: JsonTreeNodeOptions = new JsonTreeNodeOptions();

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

    isSelected(): boolean {
        return this.selected;
    }

    setSelected(isSelected: boolean) {
        this.selected = isSelected;
    }

    getOptions(): JsonTreeNodeOptions {
        return this._options;
    }
}
