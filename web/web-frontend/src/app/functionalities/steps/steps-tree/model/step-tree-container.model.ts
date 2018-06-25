
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepTreeNodeModel} from "./step-tree-node.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class StepTreeContainerModel extends StepTreeNodeModel implements JsonTreeContainer {

    children: Array<StepTreeNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;
    isComposedStepContainer: boolean;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: StepTreeContainerModel, path: Path = null, isComposedStepContainer: boolean) {
        super(parentContainer, path);
        this.jsonTreeNodeState.showChildren = true;
        this.isComposedStepContainer = isComposedStepContainer;
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

            let leftNodeText = left.isContainer() ? left.name : left.stepDef.toString();
            let rightNodeText = right.isContainer() ? right.name : right.stepDef.toString();

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
}
