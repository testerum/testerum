import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {ScenarioParamNodeModel} from "../../model/scenario-param-node.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {ScenarioParamModalService} from "./modal/scenario-param-modal.service";
import {Subscription} from "rxjs";
import {ScenarioContainerModel} from "../../model/scenario-container.model";
import {ScenarioParamType} from "../../../../../../model/test/scenario/param/scenario-param-type.enum";
import * as Prism from 'prismjs';
import 'prismjs/components/prism-json';
import {ScenarioParamChangeModel} from "./modal/model/scenario-param-change.model";

@Component({
    selector: 'scenario-param-node',
    templateUrl: 'scenario-param-node.component.html',
    styleUrls: [
        'scenario-param-node.component.scss',
        '../../../../../../generic/css/tree.scss',
    ],
    animations: [
        trigger('expandCollapse', [
            state('open', style({
                'height': '*'
            })),
            state('close', style({
                'height': '0px'
            })),
            transition('open => close', animate('400ms ease-in-out')),
            transition('close => open', animate('400ms ease-in-out'))
        ])
    ]
})
export class ScenarioParamNodeComponent implements OnInit, OnDestroy {
    @Input() model: ScenarioParamNodeModel;

    collapsed: boolean = false;
    animationState: string = 'open';

    @Input('collapsed')
    set setCollapsed(value: boolean) {
        if (this.collapsed != value) {
            this.collapsed = value;
            this.animationState = this.collapsed ? 'close' : 'open';
        }
    }

    private scenarioParamModalSubscription: Subscription;
    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService,
                private scenarioParamModalService: ScenarioParamModalService){
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        if (this.scenarioParamModalSubscription) this.scenarioParamModalSubscription.unsubscribe();
    }

    paramHasValue(): boolean {
        let paramValue = this.model.scenarioParam.value;
        return StringUtils.isNotEmpty(paramValue);
    }

    isJsonValueType(): boolean {
        return this.model.scenarioParam.type == ScenarioParamType.JSON;
    }

    isEditMode(): boolean {
        return this.scenarioTreeComponentService.isEditMode;
    }

    getHighlightedValueAsJson(): string {
        return Prism.highlight(this.model.scenarioParam.value, Prism.languages.json, 'json');
    }

    animate() {
        if(this.animationState == "close") {
            this.animationState = "open";
        } else {
            this.animationState = "close";
        }
    }

    collapse() {
        this.collapsed = true;
        this.animationState = "close";
    }

    expand() {
        this.collapsed = false;
        this.animationState = "open";
    }

    getParamUiType(): string {
        return this.model.scenarioParam.type.toUiLabel();
    }

    getParamDescription(): string {
        return "to be added";
    }

    editOrViewResourceInModal() {
        this.scenarioParamModalSubscription = this.scenarioParamModalService
            .showEditScenarioParamModal(this.model.scenarioParam, this.scenarioTreeComponentService.testModel.scenarios, this.getScenarioOfThisParam())
            .subscribe( (paramModalResult: ScenarioParamChangeModel) => {

                this.scenarioTreeComponentService.updateScenariosParams(paramModalResult, this.getScenarioOfThisParam());
        });
    }

    private getScenarioOfThisParam() {
        return (this.model.getParent().getParent() as ScenarioContainerModel).scenario;
    }
}
