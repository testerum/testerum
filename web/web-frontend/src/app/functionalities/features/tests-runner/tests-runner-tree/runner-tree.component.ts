import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';

import {TestsRunnerService} from "../tests-runner.service";
import {RunnerTreeService} from "./runner-tree.service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {RunnerRootTreeNodeModel} from "./model/runner-root-tree-node.model";
import {RunnerTreeNodeComponent} from "./nodes/runner-tree-node/runner-tree-node.component";
import {RunnerFeatureTreeNodeModel} from "./model/runner-feature-tree-node.model";
import {RunnerTestTreeNodeModel} from "./model/runner-test-tree-node.model";
import {RunnerComposedStepTreeNodeModel} from "./model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "./model/runner-basic-step-tree-node.model";
import {RunnerRootNode} from "../../../../model/runner/tree/runner-root-node.model";
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'runner-tree',
    templateUrl: 'runner-tree.component.html',
    styleUrls:['runner-tree.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnerTreeComponent implements OnInit, OnDestroy {

    @Input() treeModel: JsonTreeModel = new JsonTreeModel();
    @Input() reportMode: boolean = false;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunnerRootTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerFeatureTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerTestTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerComposedStepTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerBasicStepTreeNodeModel, RunnerTreeNodeComponent);

    startTestExecutionSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private testsRunnerService: TestsRunnerService,
                private runnerTreeService: RunnerTreeService) {
    }

    ngOnInit(): void {
        this.runnerTreeService.treeModel = this.treeModel;

        this.startTestExecutionSubscription = this.testsRunnerService.startTestExecutionObservable.subscribe((runnerRootNode: RunnerRootNode) => {
            this.refresh();
        });

        this.testsRunnerService.showTestFoldersEventObservable.subscribe((showTestFolders: boolean) => {
            this.runnerTreeService.showTestFolders(showTestFolders);
            this.refresh();
        });
        this.refresh();
    }

    ngOnDestroy(): void {
        if (this.startTestExecutionSubscription != null) {
            this.startTestExecutionSubscription.unsubscribe();
        }
    }

    stopTests() {
        this.testsRunnerService.stopExecution();
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
