import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    Type,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNode} from "../model/json-tree-node.model";
import {JsonTreeNodeState} from "../model/json-tree-node-state.model";
import {JsonContainerNodeComponent} from "./container/json-container-node.component";
import {JsonLeafNodeComponent} from "./leaf/json-leaf-node.component";
import {TreeState} from "../model/state/TreeState";

@Component({
    moduleId: module.id,
    selector: 'json-tree-node',
    templateUrl: 'json-tree-node.component.html',
    styleUrls:['json-tree-node.component.scss']
})
export class JsonTreeNodeComponent implements OnInit, OnChanges {

    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;
    @Input() allowContainerSelection: boolean = false;

    @Input() model:JsonTreeNode;
    @Input() treeState:TreeState;
    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild('childRoot', {read: ViewContainerRef}) childRoot:ViewContainerRef;

    modelComponentRef:ComponentRef<any>;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    ngOnInit(): void {
        if (!this.model) {
            return;
        }
        let componentOfModel:Type<any> = this.model.isContainer()? JsonContainerNodeComponent : JsonLeafNodeComponent;
        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(componentOfModel);

        this.modelComponentRef = this.childRoot.createComponent(factory);

        this.modelComponentRef.instance.model = this.model;
        this.modelComponentRef.instance.treeState = this.treeState;
        this.modelComponentRef.instance.modelComponentMapping = this.modelComponentMapping;
        this.modelComponentRef.instance.isLastNode = this.isLastNode;
        this.modelComponentRef.instance.isRootNode = this.isRootNode;
        this.modelComponentRef.instance.jsonTreeNodeState = new JsonTreeNodeState();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.modelComponentRef) {
            this.modelComponentRef.instance.isLastNode = this.isLastNode;
        }
    }
}
