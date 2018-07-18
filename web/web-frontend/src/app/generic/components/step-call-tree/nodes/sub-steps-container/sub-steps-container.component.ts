import {Component, Input, OnInit} from '@angular/core';
import {SubStepsContainerModel} from "../../model/sub-steps-container.model";
import {StepCallTreeService} from "../../step-call-tree.service";

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
        this.stepCallTreeService.addStepCallEditor(this.model);
        this.showChildren = true;
        this.model.jsonTreeNodeState.showChildren = true;
    }
}
