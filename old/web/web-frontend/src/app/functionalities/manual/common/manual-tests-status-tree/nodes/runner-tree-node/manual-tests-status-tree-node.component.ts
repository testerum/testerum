import {Component, Input, OnDestroy} from '@angular/core';
import {ManualUiTreeBaseStatusModel} from "../../model/manual-ui-tree-base-status.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ManualTestsStatusTreeComponentService} from "../../manual-tests-status-tree.component-service";
import {ManualTestsStatusTreeService} from "../../manual-tests-status-tree.service";
import {Subscription} from "rxjs";
import {ManualUiTreeNodeStatusModel} from "../../model/manual-ui-tree-node-status.model";
import {ManualUiTreeContainerStatusModel} from "../../model/manual-ui-tree-container-status.model";
import {ManualTestStatus} from "../../../../plans/model/enums/manual-test-status.enum";
import {UrlService} from "../../../../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'run-tree-node',
    templateUrl: 'manual-tests-status-tree-node.component.html',
    styleUrls:[
        'manual-tests-status-tree-node.component.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class ManualTestsStatusTreeNodeComponent implements OnDestroy {

    @Input() model: ManualUiTreeBaseStatusModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    ManualTestStatus = ManualTestStatus;

    constructor(private treeComponentService: ManualTestsStatusTreeComponentService,
                private manualTestsStatusTreeService: ManualTestsStatusTreeService,
                private urlService: UrlService){}

    treeFilterSubscription: Subscription;

    ngOnDestroy(): void {
        if (this.treeFilterSubscription != null) {
            this.treeFilterSubscription.unsubscribe();
        }
    }

    isFeatureNode(): boolean {
        return this.model instanceof ManualUiTreeContainerStatusModel;
    }

    isTestNode(): boolean {
        return this.model instanceof ManualUiTreeNodeStatusModel;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.status) {
            case ManualTestStatus.NOT_EXECUTED: return "Not Executed";
            case ManualTestStatus.IN_PROGRESS: return "In Progress";
            case ManualTestStatus.PASSED: return "Passed";
            case ManualTestStatus.FAILED: return "Failed";
            case ManualTestStatus.BLOCKED: return "Blocked";
            case ManualTestStatus.NOT_APPLICABLE: return "Not Applicable";

            default: return "";
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    setSelected() {
        if (this.isTestNode()) {

            if (this.treeComponentService.isNavigationTree) {
                this.urlService.navigateToManualExecPlanTestRunner(this.treeComponentService.planPath, this.model.path);
            } else {
                this.manualTestsStatusTreeService.selectNodeAtPath(this.model.path);
            }
        }
    }
}
