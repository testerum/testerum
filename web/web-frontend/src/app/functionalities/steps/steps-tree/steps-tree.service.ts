import {Injectable} from '@angular/core';
import {StepsService} from "../../../service/steps.service";
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
import {RootStepNode} from "../../../model/step/tree/root-step-node.model";

@Injectable()
export class StepsTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();

    treeFilter: StepsTreeFilter = StepsTreeFilter.createEmptyFilter();

    pathToCut: Path = null;
    pathToCopy: Path = null;

    constructor(private stepsService: StepsService,
                private jsonTreeService: JsonTreeService,){
    }

    initializeStepsTreeFromServer(selectedPath: Path = null, expandToLevel: number = 2) {
       this.stepsService.getStepsTree(this.treeFilter).subscribe((rootStepNode:RootStepNode) => {
           let newTree = StepsTreeUtil.mapRootStepToStepJsonTreeModel(rootStepNode);

           JsonTreeExpandUtil.expandTreeToLevel(newTree, expandToLevel);
           let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newTree, selectedPath);

           this.treeModel = newTree;
           this.jsonTreeService.setSelectedNode(selectedNode);
       });
    }

    sortChildren(children: Array<JsonTreeNode>) {
        children.sort((left: StepTreeNodeModel, right: StepTreeNodeModel) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            let leftNodeText = left.toString();
            let rightNodeText = right.toString();

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
        JsonTreePathUtil.copy(this.treeModel, pathToCopy, destinationPath);

        let composedRoot = (this.treeModel.children[0] as JsonTreeContainer);

        let newParent:StepTreeContainerModel = JsonTreePathUtil.getNode(
            composedRoot,
            destinationPath
        ) as StepTreeContainerModel;
        newParent.sort();
    }

    selectNodeAtPath(path: Path) {
        if (this.treeModel) {
            let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.treeModel, path);
            this.jsonTreeService.setSelectedNode(selectedNode);
        }
    }

    setPathToCut(path: Path) {
        this.pathToCopy = null;
        this.pathToCut = path;
    }

    setPathToCopy(path: Path) {
        this.pathToCopy = path;
        this.pathToCut = null;
    }

    canPaste(path: Path): boolean {
        return (this.pathToCopy != null && !this.pathToCopy.equals(path))
            || (this.pathToCut != null && !this.pathToCut.equals(path));
    }

    isPasteAStep(): boolean {
        if(this.pathToCopy && this.pathToCopy.isFile()) return true;
        if(this.pathToCut && this.pathToCut.isFile()) return true;
        return false;
    }
}
