import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    OnInit,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainer} from "../../model/json-tree-container.model";
import {JsonTreeService} from "../../json-tree.service";
import {FeatureTreeContainerModel} from "../../../../../functionalities/features/features-tree/model/feature-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'json-container-node',
    templateUrl: 'json-container-node.component.html',
    styleUrls:[
        'json-container-node.component.scss',
    ]
})
export class JsonContainerNodeComponent implements OnInit {

    @Input() model:JsonTreeContainer;

    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;

    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild('content', { read: ViewContainerRef, static: true }) content:ViewContainerRef;

    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
        let componentOfModel = this.modelComponentMapping.getComponentOfModel(this.model);
        const factory: ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(componentOfModel);

        let modelComponentRef: ComponentRef<any> = this.content.createComponent(factory);

        modelComponentRef.instance.model = this.model;
        modelComponentRef.instance.modelComponentMapping = this.modelComponentMapping;
    }

    toggleShowHideChildren() {
        if(!this.model.hasChildren()) {
            return;
        }

        this.model.getNodeState().showChildren = !this.model.getNodeState().showChildren;
        if(this.model.getNodeState().showChildren) {
            this.jsonTreeService.triggerExpendedNodeEvent(this.model);

            this.expandChildIfIsASingleContainer(this.model);
        }
    }

    toggleShowChildren() {
        this.jsonTreeService.setSelectedNode(this.model);

        this.model.getNodeState().showChildren = true;
        this.jsonTreeService.triggerExpendedNodeEvent(this.model);

        this.expandChildIfIsASingleContainer(this.model);

        return false;
    }


    private expandChildIfIsASingleContainer(parentContainer: JsonTreeContainer) {
        if(parentContainer.getChildren().length == 1 && parentContainer.getChildren()[0].isContainer()) {
            let currentNodeToExpand = parentContainer.getChildren()[0] as JsonTreeContainer;
            currentNodeToExpand.getNodeState().showChildren = true;
            this.jsonTreeService.triggerExpendedNodeEvent(currentNodeToExpand);

            this.expandChildIfIsASingleContainer(currentNodeToExpand);
        }
    }

    hasChildren(): boolean {
        return (this.model as JsonTreeContainer).hasChildren();
    }
}
