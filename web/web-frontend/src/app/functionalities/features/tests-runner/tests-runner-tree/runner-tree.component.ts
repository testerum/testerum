import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';

import {TestsRunnerService} from "../tests-runner.service";
import {RunnerTreeComponentService} from "./runner-tree.component-service";
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
    providers: [RunnerTreeComponentService]

})
export class RunnerTreeComponent implements OnInit, OnDestroy {

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunnerRootTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerFeatureTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerTestTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerComposedStepTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerBasicStepTreeNodeModel, RunnerTreeNodeComponent);

    startTestExecutionSubscription: Subscription;
    constructor(private testsRunnerService: TestsRunnerService,
                private runnerTreeComponentService: RunnerTreeComponentService) {}


    ngOnInit(): void {
        this.runnerTreeComponentService.treeModel = this.jsonTreeModel;

        this.startTestExecutionSubscription = this.testsRunnerService.startTestExecutionObservable.subscribe((runnerRootNode: RunnerRootNode) => {
            this.runnerTreeComponentService.onStartTestExecution(runnerRootNode)
        });
        this.testsRunnerService.showTestFoldersEventObservable.subscribe((showTestFolders: boolean) => {
            this.runnerTreeComponentService.showTestFolders(showTestFolders);
        })
    }

    ngOnDestroy(): void {
        if (this.startTestExecutionSubscription != null) {
            this.startTestExecutionSubscription.unsubscribe();
        }
    }

    stopTests() {
        this.testsRunnerService.stopExecution();
    }
}
