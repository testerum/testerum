import {JsonTreeContainer} from "../../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../json-tree/model/behavior/JsonTreeContainerOptions";

export class FileDirTreeContainerModel implements JsonTreeContainer {

    parent: JsonTreeContainer;
    name: string;
    absoluteJavaPath: string;
    isProject: boolean = false;
    canCreateChild: boolean;
    private children: Array<FileDirTreeContainerModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    hidden: boolean = false;
    selected: boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parent: FileDirTreeContainerModel,
                name: string = "",
                absoluteJavaPath: string = "",
                isProject: boolean = false,
                canCreateChild: boolean = true,
                private containsChildren: boolean) {
        this.parent = parent;
        this.name = name;
        this.absoluteJavaPath = absoluteJavaPath;
        this.isProject = isProject;
        this.canCreateChild = canCreateChild;
        this.jsonTreeNodeState.showChildren = false;
    }

    getParent(): JsonTreeContainer {
        return this.parent;
    }

    getChildren(): Array<FileDirTreeContainerModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.children.length > 0 || this.containsChildren;
    }

    setChildren(children: Array<FileDirTreeContainerModel>) {
        for (let child of children) {
            this.children.push(child)
        }
        this.sort()
    }

    sort() {
        this.children.sort((left: FileDirTreeContainerModel, right: FileDirTreeContainerModel) => {
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
