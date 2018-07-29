
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
import {RootStepNode} from "../../../model/step/tree/root-step-node.model";
import {ComposedContainerStepNode} from "../../../model/step/tree/composed-container-step-node.model";
import {ComposedStepNode} from "../../../model/step/tree/composed-step-node.model";
import {ArrayUtil} from "../../../utils/array.util";

@Injectable()
export class StepChooserService {

    stepPathToRemove: Path;

    treeFilter: StepsTreeFilter = StepsTreeFilter.createEmptyFilter();

    treeModel: JsonTreeModel;

    selectedStep:StepTreeNodeModel;
    selectedNodeObserver: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    constructor(private stepsService: StepsService,
                private jsonTreeService: JsonTreeService) {
        this.selectedNodeObserver.subscribe((item: JsonTreeNodeEventModel) => this.selectedStep = item.treeNode as StepTreeNodeModel);
    }

    initializeStepsTreeFromServer(selectedPath: Path = null, expandToLevel: number = 2) {
        this.stepsService.getStepsTree(this.treeFilter).subscribe(
            (rootStepNode:RootStepNode) => this.setTreeModel(this.stepPathToRemove, rootStepNode, selectedPath, expandToLevel)
        );
    }

    setTreeModel(stepPathToRemove:Path, rootStepNode:RootStepNode, selectedPath: Path = null, expandToLevel: number = 2): void {
        this.filterNodeFromRootStepNode(rootStepNode.composedStepsRoot, stepPathToRemove);
        let newTree = StepsTreeUtil.mapRootStepToStepJsonTreeModel(rootStepNode);

        JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);
        let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newTree, selectedPath);

        this.treeModel = newTree;
        this.jsonTreeService.setSelectedNode(selectedNode);
    }

    private filterNodeFromRootStepNode(composedContainerStepNode: ComposedContainerStepNode, stepPathToRemove: Path) {
        let childToRemove: ComposedStepNode = null;
        for (const child of composedContainerStepNode.children) {
            if (child.path.equals(stepPathToRemove)) {
                childToRemove = child;
            } else {
                if (child instanceof ComposedContainerStepNode) {
                    this.filterNodeFromRootStepNode(child, this.stepPathToRemove)
                }
            }
        }
        if (childToRemove != null) {
            ArrayUtil.removeElementFromArray(composedContainerStepNode.children, childToRemove);
        }
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

    setSelectedStep(selectedStep: StepTreeNodeModel) {
        this.selectedNodeObserver.emit(
            new JsonTreeNodeEventModel(selectedStep)
        );

        this.selectedStep = selectedStep;
    }
}
