import {Component, Input, OnInit} from '@angular/core';
import {ScenarioParamsContainerModel} from "../../model/scenario-params-container.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {ScenarioParamNodeModel} from "../../model/scenario-param-node.model";
import {ScenarioTreeUtil} from "../../util/scenario-tree.util";
import {ScenarioParam} from "../../../../../../model/test/scenario/param/scenario-param.model";

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

    onAddParameter() {
        let scenarioParamNode = ScenarioTreeUtil.getScenarioParamNode(this.model, new ScenarioParam());
        this.model.children.push(scenarioParamNode);
        this.model.jsonTreeNodeState.showChildren = true;
    }
}
