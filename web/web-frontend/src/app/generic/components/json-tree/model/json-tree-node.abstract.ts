
import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeNodeOptions} from "./behavior/JsonTreeNodeOptions";
import {JsonTreeContainerOptions} from "./behavior/JsonTreeContainerOptions";

export abstract class JsonTreeNodeAbstract implements JsonTreeNode {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
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

    getOptions(): JsonTreeNodeOptions {
        return this._options;
    }
}
