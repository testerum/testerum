import {Component, Input, OnInit} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../../../generic/components/json-tree/json-tree.service";
import {ManualSelectTestsTreeNodeModel} from "../../model/manual-select-tests-tree-node.model";
import {ManualSelectTestsTreeComponentService} from "../../manual-select-tests-tree.component-service";
import {ManualSelectTestsTreeContainerModel} from "../../model/manual-select-tests-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'manual-select-tests-node',
    templateUrl: 'manual-select-tests-node.component.html',
    styleUrls:['manual-select-tests-node.component.scss']
})
export class ManualSelectTestsNodeComponent implements OnInit {

    @Input() model:ManualSelectTestsTreeNodeModel;
    isSelected:boolean = false;

    constructor(private jsonTreeService:JsonTreeService,
                private selectTestsTreeRunnerService: ManualSelectTestsTreeComponentService) {
    }

    ngOnInit(): void {

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as ManualSelectTestsTreeNodeModel) == this.model;
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

        this.model.setSelected(!this.model.isSelected());

        if(this.model.parentContainer instanceof ManualSelectTestsTreeContainerModel) {
            this.model.parentContainer.calculateCheckState();
        }
    }
}
