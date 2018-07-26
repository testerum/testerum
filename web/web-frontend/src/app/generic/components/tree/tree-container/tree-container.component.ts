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
import {TreeContainerModel} from "../../../../model/infrastructure/tree-container.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'tree-container',
    templateUrl: 'tree-container.component.html',
    styleUrls:['tree-container.component.scss']
})
export class TreeContainerComponent implements OnInit {

    @Input() treeId: string;
    @Input() model:TreeContainerModel<any, any>;
    @Input() modelRenderer:any;

    @Input() showChildren:boolean = false;
    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;

    @Input() modelComponentMapping: ModelComponentMapping;

    @ViewChild('content', {read: ViewContainerRef}) content:ViewContainerRef;

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    ngOnInit(): void {
        let componentOfModel = this.modelComponentMapping.getComponentOfModel(this.model);
        const factory:ComponentFactory<any> = this.componentFactoryResolver.resolveComponentFactory(componentOfModel);
        const stepsTreeComponentRef:ComponentRef<any> = this.content.createComponent(factory);
        stepsTreeComponentRef.instance.model = this.model;
    }

    hasChildren(): boolean {
        if(!this.model) {
            return false;
        }
        let nrOfChildContainers:number = this.model.getChildContainers().length;
        let nrOfChildNodes:number = this.model.getChildNodes().length;
        return nrOfChildContainers != 0 || nrOfChildNodes != 0;
    }

    toggleShowChildren() {
        this.showChildren = !this.showChildren;
    }
}
