import {Component, Input, OnInit} from '@angular/core';
import {StepCallContainerModel} from "../../model/step-call-container.model";
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeService} from "../../step-call-tree.service";
import {StepCallEditorContainerModel} from "../../model/step-call-editor-container.model";

@Component({
    selector: 'sub-steps-container',
    templateUrl: 'sub-steps-container.component.html',
    styleUrls: [
        'sub-steps-container.component.css',
        '../step-call-tree.css',
        '../../../json-tree/json-tree.generic.css',
        '../../../../../generic/css/tree.css',
    ]
})

export class SubStepsContainerComponent implements OnInit {
    @Input() model: SubStepsContainerModel;

    hasMouseOver: boolean = false;
    showChildren: boolean = true;

    constructor(private stepCallTreeService: StepCallTreeService) {
    }

    ngOnInit() {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeService.isEditMode;
    }

    addSubStep() {
        this.showChildren = true;

        let treeSubSteps = this.model.getChildren();

        //remove the current editor if exists
        if(treeSubSteps.length > 0 &&
            treeSubSteps[treeSubSteps.length -1] instanceof StepCallEditorContainerModel) {
                treeSubSteps.splice(treeSubSteps.length -1)
        }

        let stepCallEditorContainerModel = new StepCallEditorContainerModel(
            this.model,
            treeSubSteps.length,
            null,
            false
        );
        stepCallEditorContainerModel.jsonTreeNodeState.showChildren = false;
        treeSubSteps.push(
            stepCallEditorContainerModel
        )
    }
}
