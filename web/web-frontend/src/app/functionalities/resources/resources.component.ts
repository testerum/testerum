import {Component, OnInit} from '@angular/core';
import {ResourcesTreeService} from "./tree/resources-tree.service";
import {ResourcesTreeContainer} from "./tree/model/resources-tree-container.model";
import {ResourcesContainerComponent} from "./tree/container/resources-container.component";
import {NavigationEnd, Params, Router} from "@angular/router";
import {ResourceNodeComponent} from "./tree/container/node/resource-node.component";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {ResourcesTreeNode} from "./tree/model/resources-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'resources',
    templateUrl: 'resources.component.html',
    styleUrls: ["../../generic/css/main-container.scss"]
})

export class ResourcesComponent implements OnInit {

    private isStepSelected: boolean = true;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ResourcesTreeContainer, ResourcesContainerComponent)
        .addPair(ResourcesTreeNode, ResourceNodeComponent);

    resourcesTreeService: ResourcesTreeService;

    constructor(resourcesTreeService: ResourcesTreeService,
                private router: Router) {
        this.resourcesTreeService = resourcesTreeService;
    }

    ngOnInit() {

        this.router.events
            .filter(
                event => event instanceof NavigationEnd
            )
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                let selectedPath = params['path'];
                this.isStepSelected = selectedPath != null;
            });
    }
}
