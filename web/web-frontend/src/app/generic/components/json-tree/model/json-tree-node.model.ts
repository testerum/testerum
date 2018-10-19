import {JsonTreeContainer} from "./json-tree-container.model";
import {JsonTreeNodeOptions} from "./behavior/JsonTreeNodeOptions";

export interface JsonTreeNode {
    isContainer(): boolean;
    getParent(): JsonTreeContainer;
    isHidden(): boolean;
    setHidden(isHidden: boolean);
    isSelected(): boolean;
    setSelected(isSelected: boolean);
    getOptions(): JsonTreeNodeOptions;
}
