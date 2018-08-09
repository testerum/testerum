import {Component, Input, OnInit} from '@angular/core';
import {JsonTreeNodeEventModel} from "../../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {SelectTestTreeRunnerNodeModel} from "../../model/select-test-tree-runner-node.model";
import {SelectTestsTreeRunnerService} from "../../select-tests-tree-runner.service";
import {SelectTestTreeRunnerContainerModel} from "../../model/select-test-tree-runner-container.model";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'select-test-tree-runner-node.component.html',
    styleUrls:['select-test-tree-runner-node.component.scss']
})
export class SelectTestTreeRunnerNodeComponent implements OnInit {

    @Input() model:SelectTestTreeRunnerNodeModel;
    isSelected:boolean = false;

    constructor(private jsonTreeService:JsonTreeService,
                private selectTestsTreeRunnerService: SelectTestsTreeRunnerService) {
    }

    ngOnInit(): void {

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as SelectTestTreeRunnerNodeModel) == this.model;
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
        let parentNodes: Array<SelectTestTreeRunnerContainerModel> = this.selectTestsTreeRunnerService.getParentNodesOf(
            this.model
        ) as Array<SelectTestTreeRunnerContainerModel>;
        for (const parentNode of parentNodes) {
            parentNode.calculateCheckState()
        }
    }
}
