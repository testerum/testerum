import {Component, Input, OnInit} from '@angular/core';
import {RunnerTreeNodeModel} from "../../model/runner-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeSelectedListener} from "../../event/runner-tree-node-selected.listener";
import {RunnerTreeComponentService} from "../../runner-tree.component-service";
import {RunnerComposedStepTreeNodeModel} from "../../model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "../../model/runner-basic-step-tree-node.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {RunnerFeatureTreeNodeModel} from "../../model/runner-feature-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-tree-node.component.html',
    styleUrls:[
        'runner-tree-node.component.scss',
        '../../../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerTreeNodeComponent implements OnInit {

    @Input() model:RunnerTreeNodeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    isSelected:boolean = false;

    RunnerTreeNodeStateEnum = ExecutionStatusEnum;

    constructor(private runnerTreeComponentService:RunnerTreeComponentService){}

    ngOnInit(): void {
        this.runnerTreeComponentService.selectedRunnerTreeNodeObserver.subscribe((selectedTreeNode: RunnerTreeNodeModel) => {
            this.isSelected = this.model.equals(selectedTreeNode);
        })
    }

    isNodeWithStepCall(): boolean {
        return this.model instanceof RunnerComposedStepTreeNodeModel || this.model instanceof RunnerBasicStepTreeNodeModel
    }

    isFeatureNode(): boolean {
        return this.model instanceof RunnerFeatureTreeNodeModel;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.state) {
            case ExecutionStatusEnum.WAITING: return "Waiting";
            case ExecutionStatusEnum.EXECUTING : return "Executing";
            case ExecutionStatusEnum.PASSED: return "Passed";
            case ExecutionStatusEnum.FAILED: return "Failed";
            case ExecutionStatusEnum.ERROR: return "Error";
            case ExecutionStatusEnum.UNDEFINED: return "Undefined steps";
            case ExecutionStatusEnum.SKIPPED: return "Skipped";
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    setSelected() {
        this.runnerTreeComponentService.setNodeAsSelected(this.model);
    }
}
