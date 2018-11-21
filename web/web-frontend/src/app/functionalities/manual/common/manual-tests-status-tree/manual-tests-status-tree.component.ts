import {Component, Input, OnDestroy, OnInit} from '@angular/core';

import {ManualTestsStatusTreeComponentService} from "./manual-tests-status-tree.component-service";
import {ManualTestsStatusTreeNodeComponent} from "./nodes/runner-tree-node/manual-tests-status-tree-node.component";
import {Subscription} from "rxjs";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {ManualUiTreeContainerStatusModel} from "./model/manual-ui-tree-container-status.model";
import {ManualUiTreeRootStatusModel} from "./model/manual-ui-tree-root-status.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {ManualTestsStatusTreeService} from "./manual-tests-status-tree.service";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";

@Component({
    moduleId: module.id,
    selector: 'manual-tests-status-tree',
    templateUrl: 'manual-tests-status-tree.component.html',
    styleUrls: ['manual-tests-status-tree.component.scss'],
    providers: [ManualTestsStatusTreeComponentService]
})
export class ManualTestsStatusTreeComponent implements OnInit, OnDestroy {

    @Input() isNavigationTree: boolean = false;
    private planPath: Path = null;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualUiTreeRootStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeContainerStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeNodeStatusModel, ManualTestsStatusTreeNodeComponent);

    getManualTestsStatusTreeSubscription: Subscription;
    routerEventsSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                public manualTestsStatusTreeService: ManualTestsStatusTreeService,
                private manualTestsStatusTreeComponentService: ManualTestsStatusTreeComponentService) {
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
                let planPathAsString = params['planPath'];
                let testPathAsString = params['testPath'];
                let planPath = planPathAsString != null ? Path.createInstance(planPathAsString) : null;
                let testPath = testPathAsString != null ? Path.createInstance(testPathAsString) : null;

                if (this.planPath != null && this.planPath.equals(planPath)) {
                    this.manualTestsStatusTreeService.selectNodeAtPath(testPath);
                } else {
                    this.planPath = planPath;
                    this.initTree(planPath, testPath);
                }
            });

        let planPathAsString = this.route.snapshot.params["planPath"];
        let testPathAsString = this.route.snapshot.params["testPath"];
        let planPath = planPathAsString != null ? Path.createInstance(planPathAsString) : null;
        let testPath = testPathAsString != null ? Path.createInstance(testPathAsString) : null;

        this.planPath = planPath;
        this.initTree(planPath, testPath);
    }

    private initTree(planPath: Path, testPath: Path){
        this.manualTestsStatusTreeComponentService.isNavigationTree = this.isNavigationTree;
        this.manualTestsStatusTreeComponentService.planPath = planPath;

        this.manualTestsStatusTreeService.initializeTreeFromServer(
            planPath,
            testPath,
            ManualTreeStatusFilterModel.createEmptyFilter()
        );
    }

    ngOnDestroy(): void {
        if (this.getManualTestsStatusTreeSubscription != null) this.getManualTestsStatusTreeSubscription.unsubscribe();
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
