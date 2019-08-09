import {Component, Input, OnInit} from '@angular/core';
import {ScenarioParamsContainerModel} from "../../model/scenario-params-container.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";

@Component({
    selector: 'scenario-params-container',
    templateUrl: 'scenario-params-container.component.html',
    styleUrls: [
        'scenario-params-container.component.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ScenarioParamsContainerComponent implements OnInit{
    @Input() model: ScenarioParamsContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService) {
    }

    ngOnInit(): void {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.scenarioTreeComponentService.isEditMode;
    }
}
