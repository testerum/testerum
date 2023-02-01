import {Component} from '@angular/core';
import {ResourcesTreeService} from "./tree/resources-tree.service";
import {ResourcesTreeContainer} from "./tree/model/resources-tree-container.model";
import {ResourcesContainerComponent} from "./tree/container/resources-container.component";
import {ResourceNodeComponent} from "./tree/container/node/resource-node.component";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {ResourcesTreeNode} from "./tree/model/resources-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'resources',
    templateUrl: 'resources.component.html'
})
export class ResourcesComponent {

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ResourcesTreeContainer, ResourcesContainerComponent)
        .addPair(ResourcesTreeNode, ResourceNodeComponent);

    resourcesTreeService: ResourcesTreeService;

    constructor(resourcesTreeService: ResourcesTreeService) {
        this.resourcesTreeService = resourcesTreeService;
    }
}
