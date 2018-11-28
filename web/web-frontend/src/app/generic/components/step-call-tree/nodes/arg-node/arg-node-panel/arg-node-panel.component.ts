import {Component, EventEmitter, Input, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Arg} from "../../../../../../model/arg/arg.model";
import {ResourceMapEnum} from "../../../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeComponentService} from "../../../step-call-tree.component-service";
import {Subscription} from "rxjs";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";

@Component({
    moduleId: module.id,
    selector: 'arg-node-panel',
    templateUrl: 'arg-node-panel.component.html',
    styleUrls: ["arg-node-panel.component.scss"],
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

export class ArgNodePanelComponent implements OnInit, OnDestroy {

    @Input() arg: Arg;
    @Input() paramStepPart:ParamStepPatternPart;

    collapsed: boolean = false;
    animationState: string = 'open';
    isEditMode: boolean;

    @Input('collapsed')
    set setCollapsed(value: boolean) {
        if (this.collapsed != value) {
            this.collapsed = value;
            this.animationState = this.collapsed ? 'close' : 'open';
        }
    }

    editButtonClickedEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    editModeSubscription: Subscription;
    constructor(private stepCallTreeComponentService: StepCallTreeComponentService){
    }

    ngOnInit(): void {
        this.isEditMode = this.stepCallTreeComponentService.isEditMode;
        this.editModeSubscription = this.stepCallTreeComponentService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {this.isEditMode = editMode}
        );
    }

    ngOnDestroy(): void {
        this.editModeSubscription.unsubscribe();
    }

    editOrViewResourceInModal() {
        if (!this.isEditMode) {
            this.stepCallTreeComponentService.editModeEventEmitter.emit(true);
        }
        this.editButtonClickedEventEmitter.emit();
    }

    getArgName(): string {
        if (this.arg.name) {
            return this.arg.name
        }

        if (this.paramStepPart.name) {
            return this.paramStepPart.name
        }

        if (this.arg.path && this.arg.path.fileName) {
            return this.arg.path.fileName;
        }

        return "param";
    }

    getArgUiType(): string {
        let resourceType = ResourceMapEnum.getResourceMapEnumByUiType(this.arg.uiType);
        if (resourceType) {
            return resourceType.uiName;
        }

        return this.arg.uiType;
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

    onAnimateEnd() {
    }

    hasOwnWarnings(): boolean {
        return this.arg.warnings.length > 0;
    }

    isManualStep(): boolean {
        return this.stepCallTreeComponentService.areManualSteps;
    }
}
