import {filter, map} from 'rxjs/operators';
import {Component, Input, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {ResourcesTreeService} from "./resources-tree.service";
import {UrlUtil} from "../../../utils/url.util";
import {Subscription} from "rxjs";

@Component({
    selector: 'resources-tree',
    template: `        
        <json-tree [treeModel]="treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})
export class ResourcesTreeComponent implements OnInit {

    @Input() treeModel:JsonTreeModel ;
    @Input() modelComponentMapping: ModelComponentMapping;

    routerEventsSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private resourcesTreeService: ResourcesTreeService) {
    }

    ngOnInit(): void {
        this.routerEventsSubscription = this.router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;
                return leafRoute.params
            }))
            .subscribe((params: Params) => {
                    let pathAsString = params['path'];
                    let path = pathAsString != null ? Path.createInstance(pathAsString) : null;
                    this.resourcesTreeService.selectNodeAtPath(path);
                }
            );

        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path']: null;
        let path: Path = pathAsString !=null ? Path.createInstance(pathAsString) : null;

        this.resourcesTreeService.initializeResourceTreeFromServer(path);
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
