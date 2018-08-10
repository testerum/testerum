import {Component, Input, OnInit} from '@angular/core';
import {RunnerTreeNodeModel} from "../model/runner-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeSelectedListener} from "../event/runner-tree-node-selected.listener";
import {RunnerTreeComponentService} from "../runner-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-tree-node.component.html',
    styleUrls:['runner-tree-node.component.scss']
})
export class RunnerTreeNodeComponent implements OnInit {

    @Input() model:RunnerTreeNodeModel;

    isSelected:boolean = false;

    RunnerTreeNodeStateEnum = ExecutionStatusEnum;

    constructor(private runnerTreeComponentService:RunnerTreeComponentService){}

    ngOnInit(): void {
        this.runnerTreeComponentService.selectedRunnerTreeNodeObserver.subscribe((selectedTreeNode: RunnerTreeNodeModel) => {
            this.isSelected = this.model.equals(selectedTreeNode);
        })
    }

    setNodeAsSelected() {
        this.runnerTreeComponentService.selectedRunnerTreeNodeObserver.emit(this.model);
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
}
