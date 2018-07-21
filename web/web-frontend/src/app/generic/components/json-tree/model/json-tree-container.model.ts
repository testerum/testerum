
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeNodeState} from "./json-tree-node-state.model";
import {JsonTreeContainerOptions} from "./behavior/JsonTreeContainerOptions";

export interface JsonTreeContainer extends JsonTreeNode {

    getParent(): JsonTreeContainer;
    isContainer(): boolean;
    isHidden(): boolean;
    setHidden(isHidden: boolean);

    getChildren(): Array<JsonTreeNode>;
    hasChildren(): boolean;
    getNodeState(): JsonTreeNodeState;

    getOptions(): JsonTreeContainerOptions;
}
