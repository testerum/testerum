
import {Injectable,EventEmitter} from "@angular/core";
import {BasicStepDef} from "../../../model/basic-step-def.model";
import {TreeNodeModel} from "../../../model/infrastructure/tree-node.model";
import {SelectedTreeNodeEventModel} from "./event/selected-tree-node-event.model";
@Injectable()
export class TreeService {

    selectedStep: TreeNodeModel;
    selectedStepObserver: EventEmitter<SelectedTreeNodeEventModel> = new EventEmitter<SelectedTreeNodeEventModel>();

    constructor() {
        this.selectedStepObserver.subscribe((item: SelectedTreeNodeEventModel) => this.selectedStep = item.treeNodeModel);
    }

    setSelectedStep(selectedStep: TreeNodeModel, treeId:string) {
        this.selectedStepObserver.emit(
            new SelectedTreeNodeEventModel(treeId, selectedStep)
        );

        this.selectedStep = selectedStep;
    }
}