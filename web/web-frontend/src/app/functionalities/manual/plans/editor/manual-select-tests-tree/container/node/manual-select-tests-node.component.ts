import {Component, Input, OnInit} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../../../generic/components/json-tree/json-tree.service";
import {SelectTestsTreeNodeModel} from "../../model/select-tests-tree-node.model";
import {ManualSelectTestsTreeComponentService} from "../../manual-select-tests-tree.component-service";
import {SelectTestsTreeContainerModel} from "../../model/select-tests-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'manual-select-tests-node',
    templateUrl: 'manual-select-tests-node.component.html',
    styleUrls:['manual-select-tests-node.component.scss']
})
export class ManualSelectTestsNodeComponent implements OnInit {

    @Input() model:SelectTestsTreeNodeModel;
    isSelected:boolean = false;

    constructor(private jsonTreeService:JsonTreeService,
                private selectTestsTreeRunnerService: ManualSelectTestsTreeComponentService) {
    }

    ngOnInit(): void {

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as SelectTestsTreeNodeModel) == this.model;
            }
        )
    }

    isEditMode(): boolean {
        return this.selectTestsTreeRunnerService.isEditMode;
    }

    selectOrNot() {
        if(!this.isEditMode()) {
            return;
        }

        this.model.isSelected = !this.model.isSelected;

        if(this.model.parentContainer instanceof SelectTestsTreeContainerModel) {
            this.model.parentContainer.calculateCheckState();
        }
    }
}
