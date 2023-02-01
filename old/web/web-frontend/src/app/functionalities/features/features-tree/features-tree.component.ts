import {Component, OnDestroy, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureContainerComponent} from "./container/feature-container.component";
import {FeaturesTreeService} from "./features-tree.service";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {TestNodeComponent} from "./container/node/test-node.component";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {UrlUtil} from "../../../utils/url.util";
import {filter, map} from "rxjs/operators";
import {Path} from "../../../model/infrastructure/path/path.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'features-tree',
    template: `
        <json-tree [treeModel]="featuresTreeService.treeModel"
                   [modelComponentMapping]="modelComponentMapping">
        </json-tree>
    `
})
export class FeaturesTreeComponent implements OnInit, OnDestroy {

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FeatureTreeContainerModel, FeatureContainerComponent)
        .addPair(TestTreeNodeModel, TestNodeComponent);

    featuresTreeService: FeaturesTreeService;
    routerEventsSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private treeService:JsonTreeService,
                featuresTreeService: FeaturesTreeService) {
        this.featuresTreeService = featuresTreeService;

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
                    this.featuresTreeService.selectNodeAtPath(path);
                }
            );

        let path = UrlUtil.getPathParamFromUrl(this.activatedRoute);

        this.featuresTreeService.treeFilter = FeaturesTreeFilter.createEmptyFilter();
        this.featuresTreeService.initializeTestsTreeFromServer(path);
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
