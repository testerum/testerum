import {Component, Input, OnInit} from '@angular/core';
import {ManualUiTreeNodeStatusModel} from "../model/manual-ui-tree-node-status.model";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTreeStatusFilterModel} from "../model/filter/manual-tree-status-filter.model";
import {JsonTreeExpandUtil} from "../../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {ManualTestsStatusTreeService} from "../manual-tests-status-tree.service";

@Component({
    selector: 'manual-tests-status-tree-toolbar',
    templateUrl: './manual-tests-status-tree-toolbar.component.html',
    styleUrls: ['./manual-tests-status-tree-toolbar.component.scss']
})
export class ManualTestsStatusTreeToolbarComponent implements OnInit {

    @Input() treeModel:JsonTreeModel ;

    model = new ManualTreeStatusFilterModel();

    constructor(private manualTestsStatusTreeService: ManualTestsStatusTreeService) {}

    ngOnInit() {}

    onToggleWaiting() {
        this.model.showNotExecuted = !this.model.showNotExecuted;
        this.triggerFilterChangeEvent();
    }

    onTogglePassed() {
        this.model.showPassed = !this.model.showPassed;
        this.triggerFilterChangeEvent();
    }

    onToggleFailed() {
        this.model.showFailed = !this.model.showFailed;
        this.triggerFilterChangeEvent();
    }

    onToggleBlocked() {
        this.model.showBlocked = !this.model.showBlocked;
        this.triggerFilterChangeEvent();
    }

    onToggleNotApplicable() {
        this.model.showNotApplicable = !this.model.showNotApplicable;
        this.triggerFilterChangeEvent();
    }

    private triggerFilterChangeEvent() {
        this.manualTestsStatusTreeService.treeFilterObservable.emit(this.model)
    }

    onExpandAllNodes(): void {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel,  100);
    }

    onExpandToTests() {
        JsonTreeExpandUtil.expandTreeToNodeType(this.treeModel,  ManualUiTreeNodeStatusModel);

    }
}
