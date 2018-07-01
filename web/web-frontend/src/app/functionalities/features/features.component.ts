import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {FeatureContainerComponent} from "./features-tree/container/feature-container.component";
import {TestNodeComponent} from "./features-tree/container/node/test-node.component";
import {TestTreeNodeModel} from "./features-tree/model/test-tree-node.model";
import {FeatureTreeContainerModel} from "./features-tree/model/feature-tree-container.model";
import {FeaturesTreeService} from "./features-tree/features-tree.service";

@Component({
    moduleId: module.id,
    selector: 'features',
    templateUrl: 'features.component.html',
    styleUrls: ["features.component.css", "../../generic/css/main-container.css"]
})
export class FeaturesComponent implements OnInit {

    isTestSelected: boolean = true;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FeatureTreeContainerModel, FeatureContainerComponent)
        .addPair(TestTreeNodeModel, TestNodeComponent);

    testsTreeService: FeaturesTreeService;

    constructor(testsTreeService: FeaturesTreeService,
                private router: Router) {

        this.testsTreeService = testsTreeService;
    }

    ngOnInit() {
        this.testsTreeService.initializeTestsTreeFromServer();


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
