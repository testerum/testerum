import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {ModelComponentMapping} from "../../model/infrastructure/model-component-mapping.model";
import {ManualTestsExecutorTreeContainerComponent} from "./tree/container/manual-tests-executor-tree-container.component";
import {ManualTestsExecutorTreeNodeComponent} from "./tree/container/node/manual-tests-executor-tree-node.component";
import {ManualTestsExecutorTreeService} from "./tree/manual-tests-executor-tree.service";
import {ManualTestsTreeExecutorContainerModel} from "./tree/model/manual-tests-tree-executor-container.model";
import {ManualTestsTreeExecutorNodeModel} from "./tree/model/manual-tests-tree-executor-node.model";

@Component({
    selector: 'manual-tests-executor',
    templateUrl: 'manual-tests-executor.component.html',
    styleUrls: ['manual-tests-executor.component.scss', '../../generic/css/forms.scss', "../../generic/css/main-container.scss"]
})

export class ManualTestsExecutorComponent implements OnInit {

    isTestSelected: boolean = true;

    manualTestsExecutorTreeService: ManualTestsExecutorTreeService;
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualTestsTreeExecutorContainerModel, ManualTestsExecutorTreeContainerComponent)
        .addPair(ManualTestsTreeExecutorNodeModel, ManualTestsExecutorTreeNodeComponent);

    constructor(private router: Router,
                private route: ActivatedRoute,
                manualTestsExecutorTreeService: ManualTestsExecutorTreeService) {
        this.manualTestsExecutorTreeService = manualTestsExecutorTreeService;
    }

    ngOnInit() {

        this.route.data.subscribe(data => {
            let manualTestsRunner = data['manualTestRunner'];

            this.manualTestsExecutorTreeService.initializeTestsTree(manualTestsRunner);
        });

        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                    let testPath = params['testPath'];
                    if (testPath) {
                        this.isTestSelected = true;
                    } else {
                        this.isTestSelected = false;
                    }
                }
            );
    }
}
