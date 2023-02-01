import {JsonTreeContainer} from "../../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../json-tree/model/behavior/JsonTreeContainerOptions";
import {FileTreeNode} from "./file-tree-node.model";

export class FileTreeContainer extends FileTreeNode {

    parentContainer: JsonTreeContainer;
    name: string;
    absoluteJavaPath: string;
    isProject: boolean = false;
    canCreateChild: boolean;
    private children: Array<FileTreeNode> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    hidden: boolean = false;
    selected: boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: FileTreeContainer,
                name: string = "",
                absoluteJavaPath: string = "",
                isProject: boolean = false,
                canCreateChild: boolean = true,
                private containsChildren: boolean) {
        super(parentContainer, name, absoluteJavaPath);
        this.isProject = isProject;
        this.canCreateChild = canCreateChild;
        this.jsonTreeNodeState.showChildren = false;
    }

    getParent(): JsonTreeContainer {
        return this.parentContainer;
    }

    getChildren(): Array<FileTreeNode> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.children.length > 0 || this.containsChildren;
    }

    setChildren(children: Array<FileTreeContainer>) {
        for (let child of children) {
            this.children.push(child)
        }
        this.sort()
    }

    sort() {
        this.children.sort((left: FileTreeContainer, right: FileTreeContainer) => {
            if(left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if(! left.isContainer() && right.isContainer()) {
                return 1;
            }

            if(left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if(left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });
    }

    isContainer(): boolean {
        return true;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
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
