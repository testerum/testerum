import {JsonTreeContainer} from "./json-tree-container.model";

export interface JsonTreeNode {
    isContainer(): boolean;
    getParent(): JsonTreeContainer;
}
