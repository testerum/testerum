
import {EventEmitter, Injectable, OnInit} from "@angular/core";
import {BasicStepDef} from "../../../model/basic-step-def.model";
import {StepDef} from "../../../model/step-def.model";
import {TreeNodeModel} from "../../../model/infrastructure/tree-node.model";
import {StepChooserNodeComponent} from "./step-chooser-container/step-chooser-node/step-chooser-node.component";
import {JsonTreeNodeEventModel} from "../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {JsonTreeModel} from "../json-tree/model/json-tree.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ComposedStepDef} from "../../../model/composed-step-def.model";
import {JsonTreeNode} from "../json-tree/model/json-tree-node.model";
import StepsTreeUtil from "../../../functionalities/steps/steps-tree/util/steps-tree.util";
import {JsonTreeExpandUtil} from "../json-tree/util/json-tree-expand.util";
import {StepTreeContainerModel} from "../../../functionalities/steps/steps-tree/model/step-tree-container.model";
import {StepsTreeFilter} from "../../../model/step/filter/steps-tree-filter.model";
import {StepsService} from "../../../service/steps.service";
import {JsonTreeService} from "../json-tree/json-tree.service";

@Injectable()
export class StepChooserService {

    stepIdToRemove: string;

    treeFilter: StepsTreeFilter = StepsTreeFilter.createEmptyFilter();

    basicStepsJsonTreeModel: JsonTreeModel;
    composedStepsJsonTreeModel: JsonTreeModel;

    selectedStep:StepTreeNodeModel;
    selectedNodeObserver: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    constructor(private stepsService: StepsService,
                private jsonTreeService: JsonTreeService) {
        this.selectedNodeObserver.subscribe((item: JsonTreeNodeEventModel) => this.selectedStep = item.treeNode as StepTreeNodeModel);
    }

    initializeStepsTreeFromServer(selectedPath: Path = null, expandToLevel: number = 2) {
        this.stepsService.getComposedStepDefs(this.treeFilter).subscribe(
            composedStepsDefs => this.setComposedStepsModel(this.stepIdToRemove, composedStepsDefs, selectedPath, expandToLevel)
        );

        this.stepsService.getBasicSteps(this.treeFilter).subscribe(
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

    setComposedStepsModel(stepIdToRemove:string, steps: Array<ComposedStepDef>, selectedPath: Path = null, expandToLevel: number = 2): void {
        let filteredComposedSteps = steps.filter(composedStep => !this.hasOrContainsStepsWithId(composedStep, stepIdToRemove));

        let newTree = StepsTreeUtil.mapStepsDefToStepJsonTreeModel(filteredComposedSteps, true);
        this.sort(newTree);

        JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);
        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newTree, selectedPath);

        this.composedStepsJsonTreeModel = newTree;
        this.jsonTreeService.setSelectedNode(selectedNode);
    }

    private hasOrContainsStepsWithId(composedStep: ComposedStepDef, stepIdToRemove: string) {
        if(composedStep.id == stepIdToRemove) {
            return true
        }
        return this.containsStepsWithId(composedStep, stepIdToRemove);
    }

    private containsStepsWithId(composedStep: ComposedStepDef, stepIdToRemove: string) {
        for (let stepCall of composedStep.stepCalls) {
            let childComposedStepDef = stepCall.stepDef;
            if(childComposedStepDef instanceof ComposedStepDef) {
                if(childComposedStepDef.id == stepIdToRemove) {
                    return true
                }
                let childStepContainsStepToRemove = this.containsStepsWithId(childComposedStepDef, stepIdToRemove);

                if(childStepContainsStepToRemove) {
                    return true;
                }
            }
        }
        return false;
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

    setSelectedStep(selectedStep: StepTreeNodeModel) {
        this.selectedNodeObserver.emit(
            new JsonTreeNodeEventModel(selectedStep)
        );

        this.selectedStep = selectedStep;
    }
}
