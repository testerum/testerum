import {JsonTreeNodeAbstract} from "../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualTreeStatusFilterModel} from "./filter/manual-tree-status-filter.model";
import {RunnerTreeFilterModel} from "../../../../../features/tests-runner/tests-runner-tree/model/filter/runner-tree-filter.model";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualUiTreeContainerStatusModel} from "./manual-ui-tree-container-status.model";

export abstract class ManualUiTreeBaseStatusModel extends JsonTreeNodeAbstract {

    path: Path;
    name: string;
    status: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: ManualTestStatus) {
        super(parentContainer);
        this.path = path;
        this.name = name;
        this.status = status;
    }

    calculateNodeVisibilityBasedOnFilter(filter: RunnerTreeFilterModel) {
        if (filter.showWaiting == filter.showPassed &&
            filter.showPassed == filter.showFailed &&
            filter.showFailed == filter.showDisabled &&
            filter.showDisabled == filter.showUndefined &&
            filter.showUndefined == filter.showSkipped) {

            this.hidden = false;
            this.updateParentVisibility(this);
            return;
        }

        switch (this.status) {
            case ManualTestStatus.NOT_EXECUTED: {this.hidden = !filter.showWaiting; break;}
            case ManualTestStatus.IN_PROGRESS: {this.hidden = !filter.showPassed; break;}
            case ManualTestStatus.PASSED: {this.hidden = !filter.showFailed; break;}
            case ManualTestStatus.FAILED: {this.hidden = !filter.showDisabled; break;}
            case ManualTestStatus.BLOCKED: {this.hidden = !filter.showUndefined; break;}
            case ManualTestStatus.NOT_APPLICABLE: {this.hidden = !filter.showSkipped; break;}
            default: this.hidden = false;
        }

        this.updateParentVisibility(this);
    }

    updateParentVisibility(model: ManualUiTreeBaseStatusModel) {
        if(model.getParent() instanceof JsonTreeModel) return;

        let parent = model.getParent() as ManualUiTreeContainerStatusModel;
        if(parent == null) return;

        let areAllChildrenHidden = parent.areAllChildrenHidden();
        if(parent.hidden != areAllChildrenHidden) {
            parent.hidden = areAllChildrenHidden;
            this.updateParentVisibility(parent);
        }
    }
}
