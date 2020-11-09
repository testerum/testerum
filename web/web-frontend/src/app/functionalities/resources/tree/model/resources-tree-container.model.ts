import {ResourcesTreeNode} from "./resources-tree-node.model";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ResourceType} from "./type/resource-type.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class ResourcesTreeContainer extends ResourcesTreeNode implements JsonTreeContainer {

    name: string;
    readonly path: Path;
    readonly resourceType: ResourceType;

    children: Array<ResourcesTreeNode> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    constructor(parentContainer: ResourcesTreeContainer, resourceType: ResourceType, path: Path = null) {
        super(parentContainer, resourceType, path);
        this.jsonTreeNodeState.showChildren = false;
    }

    getChildren(): Array<ResourcesTreeNode> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: ResourcesTreeNode, right: ResourcesTreeNode) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            if (left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if (left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });
    }

    isContainer(): boolean {
        return true;
    }

    getChildContainerByName(directory: string): ResourcesTreeContainer {
        for (let child of this.children) {
            if (child.isContainer() && child.name.toLowerCase() === directory.toLowerCase()) {
                return child as ResourcesTreeContainer;
            }
        }

        return null;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return new JsonTreeContainerOptions();
    }
}
