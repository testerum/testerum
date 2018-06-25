import {
    Component, Input, Type, ViewContainerRef, ViewChild, ComponentFactoryResolver,
    ComponentRef, ComponentFactory, OnInit
} from '@angular/core';
import {TreeNodeModel} from "../../../../../model/infrastructure/tree-node.model";
import {TreeService} from "../../tree.service";
import {SelectedTreeNodeEventModel} from "../../event/selected-tree-node-event.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'tree-node',
    templateUrl: 'tree-node.component.html',
    styleUrls:['tree-node.component.css']
})
export class TreeNodeComponent implements OnInit {

    @Input() treeId: string;
    @Input() model: TreeNodeModel;

    @Input() modelComponentMapping: ModelComponentMapping;

    isSelected:boolean = false;

    @ViewChild('content', {read: ViewContainerRef}) content:ViewContainerRef;

    constructor(private treeService:TreeService,
                private componentFactoryResolver: ComponentFactoryResolver){
        treeService.selectedStepObserver.subscribe((item:SelectedTreeNodeEventModel) => this.onStepSelected(item));
    }

    ngOnInit(): void {
        let componentOfModel = this.modelComponentMapping.getComponentOfModel(this.model);
        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(componentOfModel);
        const leafTreeComponentRef:ComponentRef<any> = this.content.createComponent(factory);
        leafTreeComponentRef.instance.model = this.model;
    }

    setSelected() {
        this.treeService.setSelectedStep(this.model, this.treeId);
        this.isSelected = true;
    }

    onStepSelected(selectedTreeNodeEventModel:SelectedTreeNodeEventModel) : void {
        if(selectedTreeNodeEventModel.treeNodeModel == this.model) {
            this.isSelected = true;
        }
        this.isSelected = false;
    }
}
