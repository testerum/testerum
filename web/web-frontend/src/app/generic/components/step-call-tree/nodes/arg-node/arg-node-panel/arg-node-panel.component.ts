import {Component, EventEmitter, Input, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Arg} from "../../../../../../model/arg/arg.model";
import {ResourceMapEnum} from "../../../../../../functionalities/resources/editors/resource-map.enum";
import {StepCallTreeService} from "../../../step-call-tree.service";
import {Subscription} from "rxjs/Subscription";

@Component({
    moduleId: module.id,
    selector: 'arg-node-panel',
    templateUrl: 'arg-node-panel.component.html',
    styleUrls: ["arg-node-panel.component.css"],
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
    constructor(private stepCallTreeService: StepCallTreeService){
    }

    ngOnInit(): void {
        this.isEditMode = this.stepCallTreeService.isEditMode;
        this.editModeSubscription = this.stepCallTreeService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {this.isEditMode = editMode}
        );
    }

    ngOnDestroy(): void {
        this.editModeSubscription.unsubscribe();
    }

    editOrViewResourceInModal() {
        if (!this.isEditMode) {
            this.stepCallTreeService.editModeEventEmitter.emit(true);
        }
        this.editButtonClickedEventEmitter.emit();
    }

    getArgName(): string {
        let argName = this.arg.name;
        if (!this.arg.name && this.arg.path) {
            argName = this.arg.path.fileName;
        }
        return argName;
    }

    getArgUiType(): string {
        let resourceType = ResourceMapEnum.getResourceMapEnumByServerType(this.arg.uiType);
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
}
