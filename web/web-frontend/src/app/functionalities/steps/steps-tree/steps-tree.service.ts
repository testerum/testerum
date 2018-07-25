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
import {JsonTreeContainer} from "../../../generic/components/json-tree/model/json-tree-container.model";
import {StepsTreeFilter} from "../../../model/step/filter/steps-tree-filter.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";

@Injectable()
export class StepsTreeService {

    basicStepsJsonTreeModel: JsonTreeModel = new JsonTreeModel();
    composedStepsJsonTreeModel: JsonTreeModel = new JsonTreeModel();

    treeFilter: StepsTreeFilter = StepsTreeFilter.createEmptyFilter();

    constructor(private stepsService: StepsService,
                private jsonTreeService: JsonTreeService,){
    }

    initializeStepsTreeFromServer(selectedPath: Path = null, expandToLevel: number = 2) {
        this.stepsService.getComposedStepDefs(this.treeFilter).subscribe(
            composedStepsDefs => this.setComposedStepsModel(composedStepsDefs, selectedPath, expandToLevel)
        );

        this.stepsService.getDefaultSteps(this.treeFilter).subscribe(
            steps => this.setBasicStepsModel(steps, selectedPath, expandToLevel)
        );
    }

    setBasicStepsModel(steps: Array<BasicStepDef>, selectedPath: Path, expandToLevel: number): void {
        let newTree = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(steps, false);
        this.sort(newTree);

        JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);
        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newTree, selectedPath);

        this.basicStepsJsonTreeModel = newTree;
        this.jsonTreeService.setSelectedNode(selectedNode);

    }

    setComposedStepsModel(steps: Array<ComposedStepDef>, selectedPath: Path = null, expandToLevel: number = 2): void {
        let newTree = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(steps, true);
        this.sort(newTree);

        JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);
        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newTree, selectedPath);

        this.composedStepsJsonTreeModel = newTree;
        this.jsonTreeService.setSelectedNode(selectedNode);
    }

    sort(tree: JsonTreeModel): void {
        this.sortChildren(tree.children);
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
