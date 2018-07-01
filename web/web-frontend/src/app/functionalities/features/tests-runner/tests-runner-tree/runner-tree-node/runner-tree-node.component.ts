import {
    Component, OnInit, Input
} from '@angular/core';
import {RunnerTreeNodeModel} from "../model/runner-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeSelectedListener} from "../event/runner-tree-node-selected.listener";
import {RunnerTreeService} from "../runner-tree.service";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-tree-node.component.html',
    styleUrls:['runner-tree-node.component.css']
})
export class RunnerTreeNodeComponent implements OnInit, RunnerTreeNodeSelectedListener {

    @Input() model:RunnerTreeNodeModel;

    @Input() showChildren:boolean = false;
    @Input() isLastNode:boolean = false;
    @Input() isRootNode:boolean = false;

    isSelected:boolean = false;

    RunnerTreeNodeStateEnum = ExecutionStatusEnum;

    constructor(private runnerTreeService:RunnerTreeService){}

    ngOnInit(): void {
        this.runnerTreeService.addSelectedRunnerTreeNodeListeners(this)
    }

    hasChildren(): boolean {
        if(!this.model) {
            return false;
        }
        let nrOfChildContainers:number = this.model.children.length;
        let nrOfChildNodes:number = this.model.children.length;
        return nrOfChildContainers != 0 || nrOfChildNodes != 0;
    }

    toggleShowChildren() {
        this.showChildren = !this.showChildren;
    }

    setNodeAsSelected() {
        if (this.showChildren) {
            this.toggleShowChildren();
        }

        this.runnerTreeService.setNodeAsSelected(this.model);
    }

    onRunnerTreeNodeSelected(runnerTreeNode: RunnerTreeNodeModel): void {
        this.isSelected = this.model.equals(runnerTreeNode);
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
