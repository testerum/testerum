import {Component, Input, OnInit} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {StepChooserService} from "../../step-chooser.service";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {JsonTreeService} from "../../../json-tree/json-tree.service";

@Component({
    moduleId: module.id,
    selector: 'step-chooser-node',
    templateUrl: 'step-chooser-node.component.html',
    styleUrls:['step-chooser-node.component.scss']
})
export class StepChooserNodeComponent implements OnInit {

    @Input() model:StepTreeNodeModel;
    isSelected:boolean = false;

    constructor(private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
        this.jsonTreeService.selectedNodeEmitter.subscribe( (selectedNodeEvent:JsonTreeNodeEventModel) => {
            this.isSelected = (selectedNodeEvent.treeNode as StepTreeNodeModel) == this.model;
        });
        // this.stepChooserService.selectedNodeObserver.subscribe(
        //     (selectedNodeEvent:JsonTreeNodeEventModel) => {
        //         this.isSelected = (selectedNodeEvent.treeNode as StepTreeNodeModel) == this.model;
        //     }
        // )
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    getParamUiNameByType(uiType: string): string {
        return ResourceMapEnum.getResourceMapEnumByUiType(uiType).uiName;
    }

    setNodeAsSelected() {
        // this.stepChooserService.setSelectedStep(this.model)
        this.jsonTreeService.setSelectedNode(this.model);
    }
}
