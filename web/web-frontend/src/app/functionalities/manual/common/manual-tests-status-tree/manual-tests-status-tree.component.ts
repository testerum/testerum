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

    constructor(public manualTestsStatusTreeService: ManualTestsStatusTreeService,
                private manualTestsStatusTreeComponentService: ManualTestsStatusTreeComponentService) {
    }

    ngOnInit(): void {
        this.manualTestsStatusTreeComponentService.isNavigationTree = this.isNavigationTree;
        this.manualTestsStatusTreeComponentService.planPath = this.planPath;

        this.manualTestsStatusTreeService.initializeTreeFromServer(this.planPath, ManualTreeStatusFilterModel.createEmptyFilter());
    }

    ngOnDestroy(): void {
        if (this.getManualTestsStatusTreeSubscription != null) this.getManualTestsStatusTreeSubscription.unsubscribe()
    }
}
