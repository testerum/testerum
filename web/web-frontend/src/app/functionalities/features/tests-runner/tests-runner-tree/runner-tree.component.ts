import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

import {RunnerTreeNodeModel} from "./model/runner-tree-node.model";
import {TestsRunnerService} from "../tests-runner.service";
import {RunnerTreeComponentService} from "./runner-tree.component-service";
import {RunnerRootNode} from "../../../../model/runner/tree/runner-root-node.model";
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
import {StepCallTreeUtil} from "../../../../generic/components/step-call-tree/util/step-call-tree.util";
import {RunnerTreeUtil} from "./util/runner-tree.util";

@Component({
    moduleId: module.id,
    selector: 'runner-tree',
    templateUrl: 'runner-tree.component.html',
    styleUrls:['runner-tree.component.scss'],
    providers: [RunnerTreeComponentService]

})
export class RunnerTreeComponent implements OnInit, OnChanges {

    @Input() runnerRootNode:RunnerRootNode;

    jsonTreeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StepCallContainerModel, StepCallContainerComponent)
        .addPair(StepCallEditorContainerModel, StepCallEditorContainerComponent)
        .addPair(SubStepsContainerModel, SubStepsContainerComponent)
        .addPair(ParamsContainerModel, ArgsContainerComponent)
        .addPair(ArgNodeModel, ArgNodeComponent);


    constructor(private testsRunnerService: TestsRunnerService,
                private runnerTreeComponentService: RunnerTreeComponentService) {}


    ngOnInit(): void {
        this.runnerTreeComponentService.treeModel = this.jsonTreeModel;
        this.initTree();
    }

    initTree() {
        RunnerTreeUtil.mapServerModelToTreeModel(this.runnerRootNode, this.jsonTreeModel);
        this.runnerTreeComponentService.serverModel = this.runnerRootNode
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.runnerRootNode != this.runnerTreeComponentService.serverModel) {
            this.initTree();
        }
    }
    stopTests() {
        this.testsRunnerService.stopExecution();
    }
}
