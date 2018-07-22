import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, ParamMap, Params, Route, Router} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {FeatureContainerComponent} from "./features-tree/container/feature-container.component";
import {TestNodeComponent} from "./features-tree/container/node/test-node.component";
import {TestTreeNodeModel} from "./features-tree/model/test-tree-node.model";
import {FeatureTreeContainerModel} from "./features-tree/model/feature-tree-container.model";
import {FeaturesTreeService} from "./features-tree/features-tree.service";
import {switchMap} from "rxjs/operators";
import {Path} from "../../model/infrastructure/path/path.model";

@Component({
    moduleId: module.id,
    selector: 'features',
    templateUrl: 'features.component.html',
    styleUrls: ["features.component.css", "../../generic/css/main-container.css"]
})
export class FeaturesComponent implements OnInit {

    isTestSelected: boolean = true;

    featuresTreeService: FeaturesTreeService;

    constructor(featuresTreeService: FeaturesTreeService,
                private router: Router) {
        this.featuresTreeService = featuresTreeService;
    }

    ngOnInit() {

        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                    let action = params['action'];
                    if (action) {
                        this.isTestSelected = true;
                    } else {
                        this.isTestSelected = false;
                    }
                }
            );
    }
}
