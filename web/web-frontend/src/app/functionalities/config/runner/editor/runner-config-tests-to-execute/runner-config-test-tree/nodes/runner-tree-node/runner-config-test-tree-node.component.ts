import {Component, Input, OnDestroy} from '@angular/core';
import {RunnerConfigTestTreeBaseModel} from "../../model/runner-config-test-tree-base.model";
import {RunnerConfigTestTreeComponentService} from "../../runner-config-test-tree.component-service";
import {RunnerConfigTestTreeService} from "../../runner-config-test-tree.service";
import {Subscription} from "rxjs";
import {RunnerConfigTestTreeNodeModel} from "../../model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeContainerModel} from "../../model/runner-config-test-tree-container.model";
import {ModelComponentMapping} from "../../../../../../../../model/infrastructure/model-component-mapping.model";
import {UrlService} from "../../../../../../../../service/url.service";
import {ManualTestStatus} from "../../../../../../../manual/plans/model/enums/manual-test-status.enum";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-config-test-tree-node.component.html',
    styleUrls:[
        'runner-config-test-tree-node.component.scss',
        '../../../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerConfigTestTreeNodeComponent implements OnDestroy {

    @Input() model: RunnerConfigTestTreeBaseModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    ManualTestStatus = ManualTestStatus;

    constructor(private treeComponentService: RunnerConfigTestTreeComponentService,
                private manualTestsStatusTreeService: RunnerConfigTestTreeService,
                private urlService: UrlService){}

    treeFilterSubscription: Subscription;

    ngOnDestroy(): void {
        if (this.treeFilterSubscription != null) {
            this.treeFilterSubscription.unsubscribe();
        }
    }

    isFeatureNode(): boolean {
        return this.model instanceof RunnerConfigTestTreeContainerModel;
    }

    isTestNode(): boolean {
        return this.model instanceof RunnerConfigTestTreeNodeModel;
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
    }
}
