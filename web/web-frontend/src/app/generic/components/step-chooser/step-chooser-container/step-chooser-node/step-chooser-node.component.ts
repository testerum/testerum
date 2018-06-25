import {Component, OnInit, Input} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {StepTreeNodeModel} from "../../../../../functionalities/steps/steps-tree/model/step-tree-node.model";
import {StepChooserService} from "../../step-chooser.service";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";

@Component({
    moduleId: module.id,
    selector: 'step-chooser-node',
    templateUrl: 'step-chooser-node.component.html',
    styleUrls:['step-chooser-node.component.css']
})
export class StepChooserNodeComponent implements OnInit {

    @Input() model:StepTreeNodeModel;
    private isSelected:boolean = false;

    constructor(private stepChooserService: StepChooserService) {
    }

    ngOnInit(): void {
        this.stepChooserService.selectedNodeObserver.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as StepTreeNodeModel) == this.model;
            }
        )
    }

    getName() {
       return this.model.name.split(".")[0]
    }

    getParamUiNameByType(serverType: string): string {
        return ResourceMapEnum.getResourceMapEnumByServerType(serverType).uiName;
    }

    setNodeAsSelected() {
        this.stepChooserService.setSelectedStep(this.model)
    }
}
