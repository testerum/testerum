import {Injectable} from '@angular/core';
import {BasicStepDef} from "../../../model/basic-step-def.model";
import {StepsService} from "../../../service/steps.service";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import StepsTreeUtil from "./util/steps-tree.util";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {StepTreeNodeModel} from "./model/step-tree-node.model";
import {StepTreeContainerModel} from "./model/step-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../generic/components/json-tree/util/json-tree-path.util";
import {TreeContainerModel} from "../../../model/infrastructure/tree-container.model";
import {JsonTreeContainer} from "../../../generic/components/json-tree/model/json-tree-container.model";

@Injectable()
export class StepsTreeService {

    basicStepsJsonTreeModel: JsonTreeModel = new JsonTreeModel();
    composedStepsJsonTreeModel: JsonTreeModel = new JsonTreeModel();

    constructor(private stepsService: StepsService){
    }

    initializeStepsTreeFromServer() {
        this.stepsService.getComposedStepDefs().subscribe(
            composedStepsDefs => this.setComposedStepsModel(composedStepsDefs)
        );
    }

    setBasicStepsModel(steps: Array<BasicStepDef>): void {
        this.basicStepsJsonTreeModel = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(steps, false);
        this.sort()
    }

    setComposedStepsModel(steps: Array<ComposedStepDef>): void {
        this.composedStepsJsonTreeModel =  StepsTreeUtil.mapStepsDefToStepJsonTreeModel(steps, true);
        this.sort()
    }

    sort(): void {
        this.sortChildren(this.basicStepsJsonTreeModel.children);
        this.sortChildren(this.composedStepsJsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: StepTreeNodeModel, right: StepTreeNodeModel) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            let leftNodeText = left.isContainer() ? left.name : left.stepDef.toString();
            let rightNodeText = right.isContainer() ? right.name : right.stepDef.toString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return -1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return 1;

            return 0;
        });

        children.forEach( it => {
            if(it.isContainer()) {
                this.sortChildren((it as StepTreeContainerModel).children)
            }
        })
    }

    copy(pathToCopy: Path, destinationPath: Path) {
        JsonTreePathUtil.copy(this.composedStepsJsonTreeModel, pathToCopy, destinationPath);

        let composedRoot = (this.composedStepsJsonTreeModel.children[0] as JsonTreeContainer);

        let newParent:StepTreeContainerModel = JsonTreePathUtil.getNode(
            composedRoot,
            destinationPath
        ) as StepTreeContainerModel;
        newParent.sort();
    }
}
