import {Component, Input, OnInit} from '@angular/core';
import {ParamsContainerModel} from "../../model/params-container.model";
import {StepCallTreeState} from "../../step-call-tree.state";

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
export class ArgsContainerComponent {
    @Input() model: ParamsContainerModel;
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
}
