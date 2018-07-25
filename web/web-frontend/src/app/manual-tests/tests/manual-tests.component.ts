import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {ManualTestTreeContainerComponent} from "./tests-tree/container/manual-test-tree-container.component";
import {ManualTestTreeNodeComponent} from "./tests-tree/container/node/manual-test-tree-node.component";
import {ManualTestTreeNodeModel} from "./tests-tree/model/manual-test-tree-node.model";
import {ManualTestTreeContainerModel} from "./tests-tree/model/manual-test-tree-container.model";
import {ManualTestsTreeService} from "./tests-tree/manual-tests-tree.service";

@Component({
    moduleId: module.id,
    selector: 'tests',
    templateUrl: 'manual-tests.component.html',
    styleUrls: ["manual-tests.component.scss", "../../generic/css/main-container.scss"]
})
export class ManualTestsComponent implements OnInit {

    isTestSelected: boolean = true;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualTestTreeContainerModel, ManualTestTreeContainerComponent)
        .addPair(ManualTestTreeNodeModel, ManualTestTreeNodeComponent);

    testsTreeService: ManualTestsTreeService;

    constructor(testsTreeService: ManualTestsTreeService,
                private router: Router) {

        this.testsTreeService = testsTreeService;
    }

    ngOnInit() {
        this.testsTreeService.initializeTestsTreeFromServer();


        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let router = this.router;
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
