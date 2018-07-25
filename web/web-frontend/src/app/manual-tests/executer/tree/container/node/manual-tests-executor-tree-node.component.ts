import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {JsonTreeNodeEventModel} from "../../../../../generic/components/json-tree/event/selected-json-tree-node-event.model";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {ManualTestsExecutorTreeService} from "../../manual-tests-executor-tree.service";
import {ManualTestStatus} from "../../../../model/enums/manual-test-status.enum";
import {ManualTestsTreeExecutorNodeModel} from "../../model/manual-tests-tree-executor-node.model";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'manual-tests-executor-tree-node.component.html',
    styleUrls:['manual-tests-executor-tree-node.component.scss']
})
export class ManualTestsExecutorTreeNodeComponent implements OnInit {

    @Input() model:ManualTestsTreeExecutorNodeModel;
    private isSelected:boolean = false;

    ManualTestStatus = ManualTestStatus;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private jsonTreeService:JsonTreeService,
                private manualTestsExecutorTreeService: ManualTestsExecutorTreeService) {
    }

    ngOnInit(): void {

        this.jsonTreeService.selectedNodeEmitter.subscribe(
            (selectedNodeEvent:JsonTreeNodeEventModel) => {
                this.isSelected = (selectedNodeEvent.treeNode as ManualTestsTreeExecutorNodeModel) == this.model;
            }
        )
    }

    isEditMode(): boolean {
        return this.manualTestsExecutorTreeService.isEditMode;
    }

    executeTest() {
        this.router.navigate([
            "manual/execute", {
             runnerPath : this.manualTestsExecutorTreeService.manualTestRunner.path.toString()
            },
            "test", {
                runnerPath : this.manualTestsExecutorTreeService.manualTestRunner.path.toString(),
                testPath: this.model.path.toString()}
            ]
        );
    }
}
