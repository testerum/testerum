import {JsonTreeNode} from "../json-tree-node.model";
import {JsonTreeNodeState} from "../json-tree-node-state.model";
import {JsonTreePathNode} from "./json-tree-path-node.model";
import {JsonTreeContainer} from "../json-tree-container.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeContainerOptions} from "../behavior/JsonTreeContainerOptions";

export abstract class JsonTreePathContainer extends JsonTreePathNode implements JsonTreeContainer {

    name: string;
    readonly path: Path;
    parentContainer: JsonTreePathContainer;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    constructor(parentContainer: JsonTreePathContainer, path?: Path) {
        super(parentContainer, path);
    }

    abstract getChildren(): Array<JsonTreeNode>;

    getParent(): JsonTreePathContainer {
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

    private static options: JsonTreeContainerOptions = new JsonTreeContainerOptions();
    getOptions(): JsonTreeContainerOptions {
        return JsonTreePathContainer.options;
    }
}
