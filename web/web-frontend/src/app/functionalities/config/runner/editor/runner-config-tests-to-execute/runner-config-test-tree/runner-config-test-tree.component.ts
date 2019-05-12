import {ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';

import {RunnerConfigTestTreeComponentService} from "./runner-config-test-tree.component-service";
import {RunnerConfigTestTreeNodeComponent} from "./nodes/runner-tree-node/runner-config-test-tree-node.component";
import {Subscription} from "rxjs";
import {RunnerConfigTestTreeContainerModel} from "./model/runner-config-test-tree-container.model";
import {RunnerConfigTestTreeRootModel} from "./model/runner-config-test-tree-root.model";
import {RunnerConfigTestTreeNodeModel} from "./model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeService} from "./runner-config-test-tree.service";
import {RunnerConfigTestTreeFilterModel} from "./model/filter/runner-config-test-tree-filter.model";
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";

@Component({
    moduleId: module.id,
    selector: 'runner-config-test-tree',
    templateUrl: 'runner-config-test-tree.component.html',
    styleUrls: ['runner-config-test-tree.component.scss'],
    providers: [RunnerConfigTestTreeComponentService]
})
export class RunnerConfigTestTreeComponent implements OnInit, OnDestroy {

    planPath: Path = null;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunnerConfigTestTreeRootModel, RunnerConfigTestTreeNodeComponent)
        .addPair(RunnerConfigTestTreeContainerModel, RunnerConfigTestTreeNodeComponent)
        .addPair(RunnerConfigTestTreeNodeModel, RunnerConfigTestTreeNodeComponent);

    refreshTreeSubscription: Subscription;

    constructor(private cd: ChangeDetectorRef,
                public runnerConfigTestTreeService: RunnerConfigTestTreeService,
                private runnerConfigTestTreeComponentService: RunnerConfigTestTreeComponentService) {
    }

    ngOnInit(): void {
        this.refreshTreeSubscription = this.runnerConfigTestTreeService.refreshTreeEventEmitter.subscribe( (event: null) => {
            this.refresh();
        });

        this.initTree();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    private initTree(){
        this.runnerConfigTestTreeService.treeFilter = FeaturesTreeFilter.createEmptyFilter();
        this.runnerConfigTestTreeService.initializeTreeFromServer();
    }

    ngOnDestroy(): void {
        if (this.refreshTreeSubscription != null) this.refreshTreeSubscription.unsubscribe();
    }
}
