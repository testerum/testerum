import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ResultsTreeNodeModel} from "../../model/results-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {StringUtils} from "../../../../../utils/string-utils.util";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../../service/url.service";

@Component({
    selector: 'results-tree-node',
    templateUrl: 'results-tree-node.component.html',
    styleUrls:['results-tree-node.component.scss']
})

export class ResultsTreeNodeComponent implements OnInit, OnDestroy {

    @Input() model:ResultsTreeNodeModel;
    isSelected:boolean = false;

    ExecutionStatusEnum = ExecutionStatusEnum;

    selectedNodeSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private jsonTreeService:JsonTreeService,
                private urlService: UrlService) {
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
        let minHourSec = StringUtils.substringBeforeLast(this.model.name, "-");
        minHourSec = minHourSec.split("-").join(":");
        return minHourSec;
    }

    showResultFile() {
        this.urlService.navigateToAutomatedResult(this.model.path, this.model.url);
        this.jsonTreeService.setSelectedNode(this.model);
    }

    getStatusTooltip(): string {
        switch (this.model.executionResult) {
            case ExecutionStatusEnum.WAITING: return "Waiting";
            case ExecutionStatusEnum.EXECUTING : return "Executing";
            case ExecutionStatusEnum.PASSED: return "Passed";
            case ExecutionStatusEnum.FAILED: return "Failed";
            case ExecutionStatusEnum.DISABLED: return "Disabled";
            case ExecutionStatusEnum.UNDEFINED: return "Undefined steps";
            case ExecutionStatusEnum.SKIPPED: return "Skipped";

            default: return "";
        }
    }
}
