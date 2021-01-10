import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {StepTreeContainerModel} from "../model/step-tree-container.model";
import {StepTreeNodeModel} from "../model/step-tree-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {RootStepNode} from "../../../../model/step/tree/root-step-node.model";
import {ComposedStepNode} from "../../../../model/step/tree/composed-step-node.model";
import {ComposedContainerStepNode} from "../../../../model/step/tree/composed-container-step-node.model";
import {ComposedStepStepNode} from "../../../../model/step/tree/composed-step-step-node.model";
import {BasicStepNode} from "../../../../model/step/tree/basic-step-node.model";
import {BasicContainerStepNode} from "../../../../model/step/tree/basic-container-step-node.model";
import {BasicStepStepNode} from "../../../../model/step/tree/basic-step-step-node.model";

export default class StepsTreeUtil {

    static mapRootStepToStepJsonTreeModel(rootStepNode: RootStepNode): JsonTreeModel {
        let treeModel: JsonTreeModel = new JsonTreeModel();

        let composedStepRootContainer: StepTreeContainerModel = new StepTreeContainerModel(treeModel, Path.createInstanceOfEmptyPath(), true);
        composedStepRootContainer.name = "Composed Steps";
        composedStepRootContainer.editable = true;
        composedStepRootContainer.isRootPackage = true;
        composedStepRootContainer.hasOwnOrDescendantWarnings = rootStepNode.composedStepsRoot.hasOwnOrDescendantWarnings;
        StepsTreeUtil.mapComposedStepChildren(rootStepNode.composedStepsRoot.children, composedStepRootContainer);

        let basicStepRootContainer: StepTreeContainerModel = new StepTreeContainerModel(treeModel, Path.createInstanceOfEmptyPath(), false);
        basicStepRootContainer.name = "Basic Steps";
        basicStepRootContainer.isRootPackage = true;
        basicStepRootContainer.hasOwnOrDescendantWarnings = rootStepNode.basicStepsRoot.hasOwnOrDescendantWarnings;
        StepsTreeUtil.mapBasicStepChildren(rootStepNode.basicStepsRoot.children, basicStepRootContainer);

        treeModel.children.push(composedStepRootContainer);
        treeModel.children.push(basicStepRootContainer);

        return treeModel;
    }

    private static mapComposedStepChildren(composedStepNodes: ComposedStepNode[], parentContainer: StepTreeContainerModel) {
        for (const composedStepNode of composedStepNodes) {
            if (composedStepNode instanceof ComposedContainerStepNode) {

                let composedStepTreeContainer: StepTreeContainerModel = new StepTreeContainerModel(
                    parentContainer,
                    composedStepNode.path,
                    true,
                    composedStepNode.hasOwnOrDescendantWarnings
                );
                composedStepTreeContainer.name = composedStepNode.name;
                composedStepTreeContainer.editable = true;

                parentContainer.children.push(composedStepTreeContainer);

                StepsTreeUtil.mapComposedStepChildren(composedStepNode.children, composedStepTreeContainer);
                continue;
            }

            if (composedStepNode instanceof ComposedStepStepNode) {
                let composedStepTreeNode = new StepTreeNodeModel(
                    parentContainer,
                    composedStepNode.path,
                    composedStepNode.stepDef,
                    true,
                    composedStepNode.hasOwnOrDescendantWarnings,
                    composedStepNode.isUsedStep
                );

                parentContainer.children.push(composedStepTreeNode);
            }
        }
    }
    private static mapBasicStepChildren(basicStepNodes: BasicStepNode[], parentContainer: StepTreeContainerModel) {
        for (const basicStepNode of basicStepNodes) {
            if (basicStepNode instanceof BasicContainerStepNode) {

                let basicStepTreeContainer: StepTreeContainerModel = new StepTreeContainerModel(
                    parentContainer,
                    basicStepNode.path,
                    false,
                    basicStepNode.hasOwnOrDescendantWarnings
                );
                basicStepTreeContainer.name = basicStepNode.path.directories[basicStepNode.path.directories.length - 1];

                parentContainer.children.push(basicStepTreeContainer);

                this.mapBasicStepChildren(basicStepNode.children, basicStepTreeContainer);
                continue;
            }

            if (basicStepNode instanceof BasicStepStepNode) {
                let basicStepTreeNode = new StepTreeNodeModel(
                    parentContainer,
                    basicStepNode.path,
                    basicStepNode.stepDef,
                    false,
                    basicStepNode.hasOwnOrDescendantWarnings
                );

                parentContainer.children.push(basicStepTreeNode);
            }
        }
    }
}
