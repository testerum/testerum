import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ResultFile} from "../../model/result-file.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {JsonTreeNodeEventModel} from "../../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'result-file',
    templateUrl: 'result-file.component.html',
    styleUrls:['result-file.component.scss']
})

export class ResultFileComponent implements OnInit, OnDestroy {

    @Input() model:ResultFile;
    isSelected:boolean = false;

    ExecutionStatusEnum = ExecutionStatusEnum;

    selectedNodeSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private jsonTreeService:JsonTreeService) {
        this.selectedNodeSubscription = jsonTreeService.selectedNodeEmitter.subscribe((item:JsonTreeNodeEventModel) => this.onNodeSelected(item));
    }

    ngOnInit(): void {
        this.isSelected = this.jsonTreeService.isSelectedNodeEqualsWithTreeNode(this.model);
    }

    ngOnDestroy(): void {
        if(this.selectedNodeSubscription) this.selectedNodeSubscription.unsubscribe();
    }

    onNodeSelected(selectedJsonTreeNodeEventModel:JsonTreeNodeEventModel) {
        this.isSelected = selectedJsonTreeNodeEventModel.treeNode == this.model;
    }

    getNodeText(): string {
        let result = StringUtils.substringBefore(this.model.name, "_");
        return result.split("-").join(":");
    }

    showResultFile() {
        this.router.navigate(["/automated/runner/result", {path : this.model.path.toString()} ]);
        this.jsonTreeService.setSelectedNode(this.model);
    }

    getStatusTooltip(): string {
        switch (this.model.executionResult) {
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
