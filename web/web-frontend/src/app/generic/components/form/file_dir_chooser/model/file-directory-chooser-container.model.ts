import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../json-tree/model/behavior/JsonTreeContainerOptions";

export class FileDirectoryChooserContainerModel implements JsonTreeContainer {

    parent: JsonTreeContainer;
    name: string;
    absoluteJavaPath: string;
    canCreateChild: boolean;
    private children: Array<FileDirectoryChooserContainerModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    hidden: boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parent: FileDirectoryChooserContainerModel,
                name: string = "",
                absoluteJavaPath: string = "",
                canCreateChild: boolean = true,
                private containsChildren: boolean) {
        this.parent = parent;
        this.name = name;
        this.canCreateChild = canCreateChild;
        this.absoluteJavaPath = absoluteJavaPath;
        this.jsonTreeNodeState.showChildren = false;
    }

    getParent(): JsonTreeContainer {
        return this.parent;
    }

    getChildren(): Array<FileDirectoryChooserContainerModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.children.length > 0 || this.containsChildren;
    }

    setChildren(children: Array<FileDirectoryChooserContainerModel>) {
        for (let child of children) {
            this.children.push(child)
        }
        this.sort()
    }

    sort() {
        this.children.sort((left: FileDirectoryChooserContainerModel, right: FileDirectoryChooserContainerModel) => {
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
}
