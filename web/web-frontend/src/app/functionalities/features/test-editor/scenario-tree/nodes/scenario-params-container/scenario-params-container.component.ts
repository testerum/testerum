import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ScenarioParamsContainerModel} from "../../model/scenario-params-container.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {ScenarioParamNodeModel} from "../../model/scenario-param-node.model";
import {ScenarioTreeUtil} from "../../util/scenario-tree.util";
import {ScenarioParam} from "../../../../../../model/test/scenario/param/scenario-param.model";
import {ScenarioParamModalService} from "../scenario-param-node/modal/scenario-param-modal.service";
import {Subscription} from "rxjs";
import {ScenarioContainerModel} from "../../model/scenario-container.model";

@Component({
    selector: 'scenario-params-container',
    templateUrl: 'scenario-params-container.component.html',
    styleUrls: [
        'scenario-params-container.component.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ScenarioParamsContainerComponent implements OnInit, OnDestroy {
    @Input() model: ScenarioParamsContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    private scenarioParamModalSubscription: Subscription;

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService,
                private scenarioParamModalService: ScenarioParamModalService) {
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        if (this.scenarioParamModalSubscription) this.scenarioParamModalSubscription.unsubscribe();
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isEditMode(): boolean {
        return this.scenarioTreeComponentService.isEditMode;
    }

    onAddParameter() {
        this.scenarioParamModalSubscription = this.scenarioParamModalService
            .showEditScenarioParamModal(null, this.scenarioTreeComponentService.testModel.scenarios, (this.model.getParent() as ScenarioContainerModel).scenario)
            .subscribe( (newScenarioParam: ScenarioParam|null) => {

                if (newScenarioParam != null) {
                    (this.model.parentContainer as ScenarioContainerModel).scenario.params.push(newScenarioParam);

                    let scenarioParamNode = ScenarioTreeUtil.getScenarioParamNode(this.model, newScenarioParam);

                    this.model.children.push(scenarioParamNode);
                    this.model.jsonTreeNodeState.showChildren = true;
                }
        });
    }
}
