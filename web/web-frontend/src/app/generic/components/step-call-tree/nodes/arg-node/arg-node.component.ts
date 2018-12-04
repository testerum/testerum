import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    OnInit,
    Type,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {ArgNodeModel} from "../../model/arg-node.model";
import {ResourceComponent} from "../../../../../functionalities/resources/editors/resource-component.interface";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {Subscription} from "rxjs";
import {StepCallTreeComponentService} from "../../step-call-tree.component-service";
import {ArgModalService} from "../../arg-modal/arg-modal.service";
import {ArgModalEnum} from "../../arg-modal/enum/arg-modal.enum";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
    selector: 'arg-node',
    templateUrl: 'arg-node.component.html',
    styleUrls: [
        'arg-node.component.scss',
        '../step-call-tree.scss',
        '../../../../../generic/css/tree.scss',
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
export class ArgNodeComponent implements OnInit {
    @Input() model: ArgNodeModel;

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

    private resourceComponentRef: ComponentRef<ResourceComponent<any>>;
    @ViewChild('resourceContainer', {read: ViewContainerRef}) content:ViewContainerRef;

    editModeSubscription: Subscription;
    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private stepCallTreeComponentService: StepCallTreeComponentService,
                private argModalService: ArgModalService){
    }

    ngOnInit(): void {
        this.isEditMode = this.stepCallTreeComponentService.isEditMode;
        this.editModeSubscription = this.stepCallTreeComponentService.editModeEventEmitter.subscribe(
            (editMode: boolean) => {this.isEditMode = editMode}
        );

        let paramRendererContainer: Type<any> = this.getResourceRenderer();
        const factory: ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(paramRendererContainer);
        this.resourceComponentRef = this.content.createComponent(factory);

        this.initCondensedViewMode(this.model.arg);
    }

    private initCondensedViewMode(arg) {
        this.resourceComponentRef.instance.model = arg.content.clone();
        this.resourceComponentRef.instance.name = arg.name;
        this.resourceComponentRef.instance.stepParameter = this.model.stepPatternParam;
        this.resourceComponentRef.instance.condensedViewMode = true;
        this.resourceComponentRef.instance.editMode = false;
    }

    ngOnDestroy(): void {
        this.editModeSubscription.unsubscribe();
    }

    private getResourceRenderer():Type<any>  {
        let paramTypeEnumByType = ResourceMapEnum.getResourceMapEnumByUiType(this.model.arg.uiType);
        if(paramTypeEnumByType) {
            return paramTypeEnumByType.resourceComponent;
        }

        throw new Error("Unmapped class to a resource class name [" + this.model.arg.uiType + "] ");
    }

    editOrViewResourceInModal() {
        if (!this.isEditMode) {
            this.stepCallTreeComponentService.editModeEventEmitter.emit(true);
        }

        this.argModalService.showAreYouSureModal(this.model.arg, this.model.stepPatternParam).subscribe( (event:ArgModalEnum) => {
            this.initCondensedViewMode(this.model.arg);
            this.resourceComponentRef.instance.refresh();
        });
    }

    argHasContent(): boolean {
        let arg = this.model.arg;
        return (arg !== null) && (arg.content != null) && (!arg.content.isEmpty());
    }

    getArgName(): string {
        if (this.model.arg.name) {
            return this.model.arg.name
        }

        if (this.model.stepPatternParam.name) {
            return this.model.stepPatternParam.name
        }

        if (this.model.arg.path && this.model.arg.path.fileName) {
            return this.model.arg.path.fileName;
        }

        return "param";
    }

    getArgUiType(): string {
        let resourceType = ResourceMapEnum.getResourceMapEnumByUiType(this.model.arg.uiType);
        if (resourceType) {
            return resourceType.uiName;
        }

        return this.model.arg.uiType;
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
        return this.model.arg.warnings.length > 0;
    }

    isManualExecutionMode(): boolean {
        return this.stepCallTreeComponentService.isManualExecutionMode;
    }
}
