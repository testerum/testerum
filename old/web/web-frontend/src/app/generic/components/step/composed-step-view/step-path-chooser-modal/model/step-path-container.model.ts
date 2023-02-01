
import {JsonTreeContainer} from "../../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreePathContainer} from "../../../../json-tree/model/path/json-tree-path-container.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";

export class StepPathContainerModel extends JsonTreePathContainer {

    name: string;
    readonly path: Path;
    parentContainer: JsonTreePathContainer;

    children: Array<StepPathContainerModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreePathContainer, path: Path = null, editable: boolean = true) {
        super(parentContainer, path);
        this.jsonTreeNodeState.showChildren = true;
        this.editable = true;
    }

    getChildren(): Array<StepPathContainerModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: StepPathContainerModel, right: StepPathContainerModel) => {
            if(left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if(! left.isContainer() && right.isContainer()) {
                return 1;
            }

            let leftNodeText = left.toString();
            let rightNodeText = right.toString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return -1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return 1;

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

    toString(): string {
        return this.name;
    }
}
