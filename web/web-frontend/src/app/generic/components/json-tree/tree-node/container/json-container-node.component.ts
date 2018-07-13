
import {
    Component, OnInit, Input, ViewChild, ElementRef, Type, ViewContainerRef,
    ComponentFactoryResolver, ComponentFactory, ComponentRef, Output, EventEmitter, ViewChildren, QueryList,
    AfterViewInit
} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNode} from "../../model/json-tree-node.model";
import {JsonTreeNodeState} from "../../model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../model/json-tree-container.model";
import {JsonTreeService} from "../../json-tree.service";
import {JsonTreeNodeEventModel} from "../../event/selected-json-tree-node-event.model";

@Component({
    moduleId: module.id,
    selector: 'json-container-node',
    templateUrl: 'json-container-node.component.html',
    styleUrls:[
        'json-container-node.component.css',
        '../../../json-tree/json-tree.generic.css',
    ]
})
export class JsonContainerNodeComponent implements OnInit {

    @Input() model:JsonTreeContainer;

    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;

    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild('content', {read: ViewContainerRef}) content:ViewContainerRef;

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
            this.jsonTreeService.triggerExpendedNodeEvent(this.model)
        }
    }

    toggleShowChildren() {
        this.jsonTreeService.setSelectedNode(this.model);

        this.model.getNodeState().showChildren = true;
        this.jsonTreeService.triggerExpendedNodeEvent(this.model)
    }

    hasChildren(): boolean {
        return (this.model as JsonTreeContainer).hasChildren();
    }
}
