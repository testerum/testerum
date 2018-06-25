
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../json-tree/model/json-tree-node-state.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserNodeModel} from "./path-chooser-node.model";
import {JsonTreeContainerOptions} from "../../json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreePathContainer} from "../../json-tree/model/path/json-tree-path-container.model";

export class PathChooserContainerModel extends PathChooserNodeModel implements JsonTreeContainer {

    children: Array<PathChooserNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    isNew: boolean = false;
    allowDirSelection: boolean = true;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, path: Path = null, allowDirSelection: boolean) {
        super(parentContainer, path);
        this.jsonTreeNodeState.showChildren = true;
        this.allowDirSelection = allowDirSelection;
    }

    getChildren(): Array<PathChooserNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    addChild(child: PathChooserNodeModel) {
        this.children.push(child);
        this.sort()
    }

    sort() {
        this.children.sort((left: PathChooserNodeModel, right: PathChooserNodeModel) => {
            if(left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if(! left.isContainer() && right.isContainer()) {
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
    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
