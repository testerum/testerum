import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

import {TestsRunnerService} from "../tests-runner.service";
import {RunnerTreeComponentService} from "./runner-tree.component-service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {StepCallContainerModel} from "../../../../generic/components/step-call-tree/model/step-call-container.model";
import {StepCallContainerComponent} from "../../../../generic/components/step-call-tree/nodes/step-call-container/step-call-container.component";
import {StepCallEditorContainerModel} from "../../../../generic/components/step-call-tree/model/step-call-editor-container.model";
import {StepCallEditorContainerComponent} from "../../../../generic/components/step-call-tree/nodes/step-call-editor-container/step-call-editor-container.component";
import {SubStepsContainerModel} from "../../../../generic/components/step-call-tree/model/sub-steps-container.model";
import {SubStepsContainerComponent} from "../../../../generic/components/step-call-tree/nodes/sub-steps-container/sub-steps-container.component";
import {ParamsContainerModel} from "../../../../generic/components/step-call-tree/model/params-container.model";
import {ArgsContainerComponent} from "../../../../generic/components/step-call-tree/nodes/args-container/args-container.component";
import {ArgNodeModel} from "../../../../generic/components/step-call-tree/model/arg-node.model";
import {ArgNodeComponent} from "../../../../generic/components/step-call-tree/nodes/arg-node/arg-node.component";
import {RunnerTreeUtil} from "./util/runner-tree.util";
import {RunnerRootTreeNodeModel} from "./model/runner-root-tree-node.model";
import {RunnerTreeNodeComponent} from "./nodes/runner-tree-node/runner-tree-node.component";
import {RunnerFeatureTreeNodeModel} from "./model/runner-feature-tree-node.model";
import {RunnerTestTreeNodeModel} from "./model/runner-test-tree-node.model";
import {RunnerComposedStepTreeNodeModel} from "./model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "./model/runner-basic-step-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'runner-tree',
    templateUrl: 'runner-tree.component.html',
    styleUrls:['runner-tree.component.scss'],
    providers: [RunnerTreeComponentService]

})
export class RunnerTreeComponent implements OnInit {

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(RunnerRootTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerFeatureTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerTestTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerComposedStepTreeNodeModel, RunnerTreeNodeComponent)
        .addPair(RunnerBasicStepTreeNodeModel, RunnerTreeNodeComponent);


    constructor(private testsRunnerService: TestsRunnerService,
                private runnerTreeComponentService: RunnerTreeComponentService) {}


    ngOnInit(): void {
        this.runnerTreeComponentService.treeModel = this.jsonTreeModel;
    }

    stopTests() {
        this.testsRunnerService.stopExecution();
    }
}
