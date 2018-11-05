import {Component, Input, OnDestroy, OnInit} from '@angular/core';

import {ManualTestsStatusTreeComponentService} from "./manual-tests-status-tree.component-service";
import {ManualTestsStatusTreeNodeComponent} from "./nodes/runner-tree-node/manual-tests-status-tree-node.component";
import {Subscription} from "rxjs";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualExecPlansService} from "../../service/manual-exec-plans.service";
import {ManualTestsStatusTreeUtil} from "./util/manual-tests-status-tree.util";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {ManualUiTreeContainerStatusModel} from "./model/manual-ui-tree-container-status.model";
import {ManualUiTreeRootStatusModel} from "./model/manual-ui-tree-root-status.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {ManualTestsStatusTreeRoot} from "../../plans/model/status-tree/manual-tests-status-tree-root.model";
import {ManualTestsStatusTreeService} from "./manual-tests-status-tree.service";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {UrlUtil} from "../../../../utils/url.util";
import {filter, map} from "rxjs/operators";

@Component({
    moduleId: module.id,
    selector: 'manual-tests-status-tree',
    templateUrl: 'manual-tests-status-tree.component.html',
    styleUrls: ['manual-tests-status-tree.component.scss'],
    providers: [ManualTestsStatusTreeComponentService]
})
export class ManualTestsStatusTreeComponent implements OnInit, OnDestroy {

    @Input() planPath: Path;
    @Input() isNavigationTree: boolean = false;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualUiTreeRootStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeContainerStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeNodeStatusModel, ManualTestsStatusTreeNodeComponent);

    getManualTestsStatusTreeSubscription: Subscription;
    routerEventsSubscription: Subscription;

    constructor(private router: Router,
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
                    let pathAsString = params['testPath'];
                    let path = pathAsString != null ? Path.createInstance(pathAsString) : null;
                    this.manualTestsStatusTreeService.selectNodeAtPath(path);
                }
            );

        this.manualTestsStatusTreeComponentService.isNavigationTree = this.isNavigationTree;
        this.manualTestsStatusTreeComponentService.planPath = this.planPath;

        this.activatedRoute.params.subscribe(params => {
            let testPathAsString = params['testPath'];
            this.manualTestsStatusTreeService.initializeTreeFromServer(this.planPath, Path.createInstance(testPathAsString), ManualTreeStatusFilterModel.createEmptyFilter());

        });

    }

    ngOnDestroy(): void {
        if (this.getManualTestsStatusTreeSubscription != null) this.getManualTestsStatusTreeSubscription.unsubscribe();
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
