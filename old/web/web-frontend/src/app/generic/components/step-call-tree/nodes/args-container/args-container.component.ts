import {Component, Input, OnInit} from '@angular/core';
import {ParamsContainerModel} from "../../model/params-container.model";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    selector: 'args-container',
    templateUrl: 'args-container.component.html',
    styleUrls: [
        'args-container.component.scss',
        '../step-call-tree.scss',
        '../../../../../generic/css/tree.scss',
    ]
})
export class ArgsContainerComponent {
    @Input() model: ParamsContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;
    showChildren: boolean = true;

    constructor(private stepCallTreeComponentService: StepCallTreeComponentService) {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.stepCallTreeComponentService.isEditMode;
    }
}
