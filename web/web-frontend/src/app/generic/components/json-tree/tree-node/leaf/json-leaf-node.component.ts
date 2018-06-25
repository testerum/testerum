
import {
    Component, OnInit, Input, ViewChild, ViewContainerRef,
    ComponentFactoryResolver, ComponentFactory, ComponentRef, Output, EventEmitter, OnDestroy,
} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNode} from "../../model/json-tree-node.model";
import {JsonTreeService} from "../../json-tree.service";
import {JsonTreeNodeEventModel} from "../../event/selected-json-tree-node-event.model";
import {Subscription} from "rxjs/Subscription";

@Component({
    moduleId: module.id,
    selector: 'json-leaf-node',
    templateUrl: 'json-leaf-node.component.html',
    styleUrls:[
        'json-leaf-node.component.css',
        '../container/json-container-node.component.css'
    ]
})
export class JsonLeafNodeComponent implements OnInit, OnDestroy {

    @Input() model:JsonTreeNode;

    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;

    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild('content', {read: ViewContainerRef}) content:ViewContainerRef;

    isSelected:boolean = false;

    selectedNodeSubscription: Subscription;
    constructor(private componentFactoryResolver: ComponentFactoryResolver,
                private jsonTreeService: JsonTreeService) {
    }

    ngOnInit(): void {
        let componentOfModel = this.modelComponentMapping.getComponentOfModel(this.model);
        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(componentOfModel);

        let modelComponentRef:ComponentRef<any> = this.content.createComponent(factory);

        modelComponentRef.instance.model = this.model;
        modelComponentRef.instance.modelComponentMapping = this.modelComponentMapping;

        this.selectedNodeSubscription = this.jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
    }

    onStepSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) : void {
        if(selectedJsonTreeNodeEventModel.treeNode == this.model) {
            this.isSelected = true;
        } else {
            this.isSelected = false;
        }
    }

    setSelected() {
        this.jsonTreeService.setSelectedNode(this.model);
    }
}
