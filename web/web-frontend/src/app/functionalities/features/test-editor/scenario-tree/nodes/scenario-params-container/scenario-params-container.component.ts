import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ScenarioParamsContainerModel} from "../../model/scenario-params-container.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {ScenarioParamModalService} from "../scenario-param-node/modal/scenario-param-modal.service";
import {Subscription} from "rxjs";
import {ScenarioContainerModel} from "../../model/scenario-container.model";
import {ScenarioParamChangeModel} from "../scenario-param-node/modal/model/scenario-param-change.model";

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
            .subscribe( (paramChangeModel: ScenarioParamChangeModel) => {

                this.scenarioTreeComponentService.updateScenariosParams(paramChangeModel, this.getScenarioOfThisParam());
            });
    }

    private getScenarioOfThisParam() {
        return (this.model.getParent() as ScenarioContainerModel).scenario;
    }
}
