import {StepsPackageModel} from "../../../../model/steps-package.model";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {StepDef} from "../../../../model/step-def.model";
import {BasicStepDef} from "../../../../model/basic-step-def.model";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {StepTreeContainerModel} from "../model/step-tree-container.model";
import {StepTreeNodeModel} from "../model/step-tree-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../generic/components/json-tree/util/json-tree-path.util";
import {TreeModel} from "../../../../generic/components/tree/model/tree.model";
import {PathUtil} from "../../../../utils/path.util";

export default class StepsTreeUtil {

    static mapStepsDefToStepJsonTreeModel(steps: Array<StepDef>, isComposedStep: boolean): JsonTreeModel {

        let orderedSteps = this.orderStepsByPhase(steps);

        let rootPackage: StepTreeContainerModel = new StepTreeContainerModel(null, new Path([], null, null), isComposedStep);
        rootPackage.isRootPackage = true;
        if (isComposedStep) {
            rootPackage.name = "Composed Steps";
            rootPackage.editable = true;
        } else {
            rootPackage.name = "Basic Steps";
            rootPackage.editable = false;
        }

        for (let step of steps) {
            let stepPath = step.path;
            let stepContainer: StepTreeContainerModel = this.getOrCreateStepTreeContainerWithName(stepPath, rootPackage, isComposedStep);

            stepContainer.children.push(
                StepsTreeUtil.getStepTreeNode(stepContainer, step)
            );
        }

        let treeModel: JsonTreeModel = new JsonTreeModel();
        treeModel.children = [rootPackage];
        rootPackage.parentContainer = treeModel;

        return treeModel;
    }


    private static getOrCreateStepTreeContainerWithName(stepPath: Path,
                                                        parentContainer: StepTreeContainerModel,
                                                        isComposedStep: boolean): StepTreeContainerModel {
        if (!stepPath) {
            return parentContainer;
        }

        let result: StepTreeContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = StepsTreeUtil.getOrCreateContainer(pathDirectory, result, isComposedStep)
        }
        return result;
    }

    private static getOrCreateContainer(childContainerName: string, parentContainer: StepTreeContainerModel, isComposedStep: boolean) {
        let childContainer: StepTreeContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children)  as StepTreeContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new StepTreeContainerModel(parentContainer, childContainerPath, isComposedStep);
            if (isComposedStep) {
                childContainer.editable = true;
            }
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private static orderStepsByPhase(steps: Array<StepDef>): Array<StepDef> {
        return steps.sort(
            (n1: StepDef, n2: StepDef) => {

                return n1.phase - n2.phase
            }
        )
    }

    private static getStepTreeNode(parentContainer: StepTreeContainerModel, stepDef: StepDef): StepTreeNodeModel {
        let node = new StepTreeNodeModel(parentContainer, Path.createInstanceFromPath(stepDef.path));
        node.stepDef = stepDef;
        return node;
    }
}
