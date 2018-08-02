import {Component, Input} from '@angular/core';
import {ManualTestsExecutorTreeService} from "../manual-tests-executor-tree.service";
import {ManualTestStatus} from "../../../model/enums/manual-test-status.enum";
import {ManualTestsTreeExecutorContainerModel} from "../model/manual-tests-tree-executor-container.model";

@Component({
    moduleId: module.id,
    selector: 'feature-container',
    templateUrl: 'manual-tests-executor-tree-container.component.html',
    styleUrls: [
        'manual-tests-executor-tree-container.component.scss',
        '../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../generic/css/tree.scss'
    ]
})
export class ManualTestsExecutorTreeContainerComponent {

    @Input() model: ManualTestsTreeExecutorContainerModel;
    hasMouseOver: boolean = false;
    ManualTestStatus = ManualTestStatus;

    constructor(private selectTestsTreeRunnerService: ManualTestsExecutorTreeService) {
    }

    isEditMode(): boolean {
        return this.selectTestsTreeRunnerService.isEditMode;
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
