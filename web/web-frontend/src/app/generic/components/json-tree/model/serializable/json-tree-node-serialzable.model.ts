import {JsonTreeNode} from "../json-tree-node.model";
import {JsonTreeContainer} from "../json-tree-container.model";
import {JsonTreeNodeOptions} from "../behavior/JsonTreeNodeOptions";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";

export abstract class JsonTreeNodeSerializable implements JsonTreeNode, Serializable<any> {

    parentContainer: JsonTreeContainer;
    hidden: boolean;
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

    abstract deserialize(input: Object): any;
    abstract serialize(): string;
}
