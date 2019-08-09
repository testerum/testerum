import {Component, Input, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {ScenarioParamNodeModel} from "../../model/scenario-param-node.model";
import {ScenarioTreeComponentService} from "../../scenario-tree.component-service";
import {StringUtils} from "../../../../../../utils/string-utils.util";

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
export class ScenarioParamNodeComponent implements OnInit {
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

    constructor(private scenarioTreeComponentService: ScenarioTreeComponentService){
    }

    ngOnInit(): void {
    }

    argHasContent(): boolean {
        let paramValue = this.model.scenarioParam.value;
        return StringUtils.isNotEmpty(paramValue);
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
        return this.model.scenarioParam.type.toString();
    }

    getParamDescription(): string {
        return "to be added";
    }

    editOrViewResourceInModal() {

    }
}
