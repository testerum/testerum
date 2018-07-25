import {Component, Input, OnInit} from '@angular/core';
import {StepCallTreeService} from "../../step-call-tree.service";
import {ParamsContainerModel} from "../../model/params-container.model";

@Component({
    selector: 'args-container',
    templateUrl: 'args-container.component.html',
    styleUrls: [
        'args-container.component.scss',
        '../step-call-tree.scss',
        '../../../json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss',
    ]
})
export class ArgsContainerComponent implements OnInit {
    @Input() model: ParamsContainerModel;

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
}
