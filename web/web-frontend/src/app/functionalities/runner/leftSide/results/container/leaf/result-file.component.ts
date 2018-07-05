import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {ResultFile} from "../../model/result-file.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {StringUtils} from "../../../../../../utils/string-utils.util";
import {JsonTreeNodeEventModel} from "../../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {ResourcesTreeNode} from "../../../../../resources/tree/model/resources-tree-node.model";

@Component({
    selector: 'result-file',
    templateUrl: 'result-file.component.html',
    styleUrls:['result-file.component.css']
})

export class ResultFileComponent implements OnInit {

    @Input() model:ResultFile;
    private isSelected:boolean = false;

    ExecutionStatusEnum = ExecutionStatusEnum;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private treeService:JsonTreeService) {
    }

    ngOnInit(): void {
        this.activatedRoute.children.forEach(
            (childActivateRoute: ActivatedRoute) => {
                childActivateRoute.params.subscribe( (params: Params) => {
                    let selectedPath = params['path'];

                    if(selectedPath == this.model.path.toString()){
                        this.treeService.setSelectedNode(this.model);
                    }
                })
            }
        );

        this.treeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = selectedNodeEvent.treeNode == this.model;

            }
        );
        if(this.treeService.selectedNode != null && this.treeService.selectedNode == this.model) {
            this.isSelected = true;
        }
    }

    getNodeText(): string {
        let result = StringUtils.substringBefore(this.model.name, "_");
        return result.split("-").join(":");
    }

    showResultFile() {
        this.router.navigate(["/automated/runner/result", {path : this.model.path.toString()} ]);
        this.treeService.setSelectedNode(this.model);
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
