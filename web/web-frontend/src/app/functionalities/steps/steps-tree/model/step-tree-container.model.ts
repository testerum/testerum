
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepTreeNodeModel} from "./step-tree-node.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreePathContainer} from "../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class StepTreeContainerModel extends StepTreeNodeModel implements JsonTreeContainer {

    name: string;

    children: Array<StepTreeNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;
    isComposedStepContainer: boolean;

    hasOwnOrDescendantWarnings: boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreePathContainer, path: Path = null, isComposedStepContainer: boolean, hasOwnOrDescendantWarnings: boolean = false) {
        super(parentContainer, path, null, isComposedStepContainer, hasOwnOrDescendantWarnings);
        this.jsonTreeNodeState.showChildren = true;
        this.isComposedStepContainer = isComposedStepContainer;
        this.hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings;
    }

    getChildren(): Array<StepTreeNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: StepTreeNodeModel, right: StepTreeNodeModel) => {
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

    getChildContainerByName(directory: string): StepTreeContainerModel {
        for (let child of this.children) {
            if (child.isContainer() && child.name.toLowerCase() === directory.toLowerCase()) {
                return child as StepTreeContainerModel;
            }
        }

        return null;
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
