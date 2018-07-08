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
import {StepCallTreeService} from "../../step-call-tree.service";
import {ArgNodeModel} from "../../model/arg-node.model";
import {ResourceComponent} from "../../../../../functionalities/resources/editors/resource-component.interface";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";
import {ArgNodePanelComponent} from "./arg-node-panel/arg-node-panel.component";
import {Arg} from "../../../../../model/arg/arg.model";
import {Subscription} from "rxjs/Subscription";

@Component({
    selector: 'arg-node',
    templateUrl: 'arg-node.component.html',
    styleUrls: [
        'arg-node.component.css',
        '../step-call-tree.css',
        '../../../json-tree/json-tree.generic.css',
        '../../../../../generic/css/tree.css',
    ]
})
export class ArgNodeComponent implements OnInit {
    @Input() model: ArgNodeModel;

    hasMouseOver: boolean = false;
    showChildren: boolean = true;

    @ViewChild(ArgNodePanelComponent) argNodePanelComponent:ArgNodePanelComponent;
    @ViewChild('resourceContainer', {read: ViewContainerRef}) content:ViewContainerRef;

    private afterUpdateSubscription: Subscription;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private stepCallTreeService: StepCallTreeService) {
    }

    ngOnInit() {
        let paramRendererContainer: Type<any> = this.getResourceRenderer();

        const factory: ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(paramRendererContainer);
        let resourceComponentRef: ComponentRef<ResourceComponent<any>> = this.content.createComponent(factory);
        resourceComponentRef.instance.model = this.model.arg.content;
        resourceComponentRef.instance.name = this.model.arg.name;
        resourceComponentRef.instance.stepParameter = this.model.stepPatternParam;
        resourceComponentRef.instance.condensedViewMode = true;
        resourceComponentRef.instance.editMode = false;

        this.argNodePanelComponent.editButtonClickedEventEmitter.subscribe(event => {
            let argModal = this.stepCallTreeService.argModal;
            argModal.arg = this.model.arg;
            argModal.stepParameter = this.model.stepPatternParam;
            argModal.show();
        });

        let argModal = this.stepCallTreeService.argModal;
        this.afterUpdateSubscription = argModal.afterUpdateEventEmitter.subscribe(event =>{
            resourceComponentRef.instance.refresh();
        });
    }
    ngOnDestroy(): void {
        if (this.afterUpdateSubscription) this.afterUpdateSubscription.unsubscribe();
    }

    getArg(): Arg {
        return this.model.arg
    }

    argHasContent(): boolean {
        let arg = this.getArg();
        return (arg !== null) && (!arg.content.isEmpty());
    }

    private getResourceRenderer():Type<any>  {
        let paramTypeEnumByType = ResourceMapEnum.getResourceMapEnumByServerType(this.model.arg.uiType);
        if(paramTypeEnumByType) {
            return paramTypeEnumByType.resourceComponent;
        }

        throw new Error("Unmapped class to a resource class name [" + this.model.arg.uiType + "] ");
    }

    isEditMode(): boolean {
        return this.stepCallTreeService.isEditMode;
    }
}
