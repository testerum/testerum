import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {UrlUtil} from "../../../utils/url.util";
import {StepsTreeService} from "./steps-tree.service";
import {filter, map} from "rxjs/operators";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'steps-tree',
    template: `
        <json-tree [treeModel]="treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})
export class StepsTreeComponent implements OnInit, OnDestroy {

    @Input() treeModel: JsonTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    routerEventsSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private stepTreeService: StepsTreeService) {
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
                    this.stepTreeService.selectNodeAtPath(path);
                }
            );
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
