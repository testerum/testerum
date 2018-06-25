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

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private stepCallTreeService: StepCallTreeService) {
    }

    ngOnInit() {
        let paramRendererContainer: Type<any> = this.getResourceRenderer();

        const factory: ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(paramRendererContainer);
        let parameterInputInterfaceRef: ComponentRef<ResourceComponent<any>> = this.content.createComponent(factory);
        parameterInputInterfaceRef.instance.model = this.model.arg.content;
        parameterInputInterfaceRef.instance.name = this.model.arg.name;
        parameterInputInterfaceRef.instance.stepParameter = this.model.stepPatternParam;
        parameterInputInterfaceRef.instance.condensedViewMode = true;
        parameterInputInterfaceRef.instance.editMode = false;

        this.argNodePanelComponent.editButtonClickedEventEmitter.subscribe(event => {
            let argModal = this.stepCallTreeService.argModal;
            argModal.arg = this.model.arg;
            argModal.stepParameter = this.model.stepPatternParam;
            argModal.show();
        });
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
