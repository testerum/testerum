import {ComposedContainerStepNode} from "../../../../../../model/step/tree/composed-container-step-node.model";
import {JsonTreeModel} from "../../../../json-tree/model/json-tree.model";
import {StepPathContainerModel} from "../model/step-path-container.model";
import {ComposedStepNode} from "../../../../../../model/step/tree/composed-step-node.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";

export default class StepsPathTreeUtil {

    static mapToTreeModel(rootNode: ComposedContainerStepNode, treeModel: JsonTreeModel) {

        let rootContainer: StepPathContainerModel = new StepPathContainerModel(treeModel, rootNode.path);
        rootContainer.name = "Composed Steps";
        rootContainer.editable = true;
        rootContainer.isRootPackage = true;
        StepsPathTreeUtil.mapChildren(rootNode.children, rootContainer);

        treeModel.children.push(rootContainer);

    }

    private static mapChildren(composedStepNodes: ComposedStepNode[], parentContainer: StepPathContainerModel) {
        for (const composedStepNode of composedStepNodes) {
            let composedContainerStepNode = composedStepNode as ComposedContainerStepNode;

            let childContainer: StepPathContainerModel = new StepPathContainerModel(
                parentContainer,
                composedContainerStepNode.path,
            );
            childContainer.name = composedContainerStepNode.name;
            childContainer.editable = true;

            parentContainer.children.push(childContainer);

            StepsPathTreeUtil.mapChildren(composedContainerStepNode.children, childContainer);
        }
    }
}
