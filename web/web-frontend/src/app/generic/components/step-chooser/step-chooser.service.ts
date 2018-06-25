
import {EventEmitter, Injectable, OnInit} from "@angular/core";
import {BasicStepDef} from "../../../model/basic-step-def.model";
import {StepDef} from "../../../model/step-def.model";
import {TreeNodeModel} from "../../../model/infrastructure/tree-node.model";
import {StepChooserNodeComponent} from "./step-chooser-container/step-chooser-node/step-chooser-node.component";
import {JsonTreeNodeEventModel} from "../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../functionalities/steps/steps-tree/model/step-tree-node.model";

@Injectable()
export class StepChooserService {
    selectedStep:StepTreeNodeModel;
    selectedNodeObserver: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    constructor() {
        this.selectedNodeObserver.subscribe((item: JsonTreeNodeEventModel) => this.selectedStep = item.treeNode as StepTreeNodeModel);
    }

    setSelectedStep(selectedStep: StepTreeNodeModel) {
        this.selectedNodeObserver.emit(
            new JsonTreeNodeEventModel(selectedStep)
        );

        this.selectedStep = selectedStep;
    }
}
