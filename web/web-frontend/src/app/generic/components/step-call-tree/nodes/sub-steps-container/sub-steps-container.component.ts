import {Component, Input, OnInit} from '@angular/core';
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeState} from "../../step-call-tree.state";

@Component({
    selector: 'sub-steps-container',
    templateUrl: 'sub-steps-container.component.html',
    styleUrls: [
        'sub-steps-container.component.scss',
        '../step-call-tree.scss',
        '../../../json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss',
    ]
})

export class SubStepsContainerComponent {
    @Input() model: SubStepsContainerModel;
    @Input() treeState: StepCallTreeState;

    hasMouseOver: boolean = false;
    showChildren: boolean = true;

    constructor() {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.treeState.isEditMode;
    }

    addSubStep() {
        this.treeState.addStepCallEditor(this.model);
        this.showChildren = true;
        this.model.jsonTreeNodeState.showChildren = true;
    }
}
