import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';

import {TestsRunnerService} from "../tests-runner.service";
import {ManualTestsStatusTreeComponentService} from "./manual-tests-status-tree.component-service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {ManualTestsStatusTreeNodeComponent} from "./nodes/runner-tree-node/manual-tests-status-tree-node.component";
import {ManualUiTreeBaseStatusModel} from "./model/manual-ui-tree-base-status.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {RunnerComposedStepTreeNodeModel} from "./model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "./model/runner-basic-step-tree-node.model";
import {RunnerRootNode} from "../../../../model/runner/tree/runner-root-node.model";
import {Subscription} from "rxjs";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualExecPlansService} from "../../../service/manual-exec-plans.service";
import {ManualTestsStatusTreeRoot} from "../../model/status-tree/manual-tests-status-tree-root.model";

@Component({
    moduleId: module.id,
    selector: 'manual-tests-status-tree',
    templateUrl: 'manual-tests-status-tree.component.html',
    styleUrls:['manual-tests-status-tree.component.scss'],
    providers: [ManualTestsStatusTreeComponentService]
})
export class ManualTestsStatusTreeComponent implements OnInit, OnDestroy {

    @Input() path: Path;
    treeModel: JsonTreeModel = new JsonTreeModel();

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualUiTreeNodeStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeBaseStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(ManualUiTreeNodeStatusModel, ManualTestsStatusTreeNodeComponent)
        .addPair(RunnerComposedStepTreeNodeModel, ManualTestsStatusTreeNodeComponent)
        .addPair(RunnerBasicStepTreeNodeModel, ManualTestsStatusTreeNodeComponent);

    constructor(private manualExecPlanService: ManualExecPlansService) {}

    ngOnInit(): void {
        this.manualExecPlanService.getManualTestsStatusTree(this.path).subscribe((manualTestsStatusTreeRoot: ManualTestsStatusTreeRoot) => {

        });
    }

    ngOnDestroy(): void {

    }

}
