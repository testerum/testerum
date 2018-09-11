import {EventEmitter, Injectable} from "@angular/core";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ManualUiTreeBaseStatusModel} from "./model/manual-ui-tree-base-status.model";
import {ManualUiTreeRootStatusModel} from "./model/manual-ui-tree-root-status.model";

@Injectable()
export class ManualTestsStatusTreeComponentService {

    isNavigationTree: boolean = false;

    selectedRunnerTreeNode: ManualUiTreeNodeStatusModel;
    selectedRunnerTreeNodeObserver: EventEmitter<ManualUiTreeNodeStatusModel> = new EventEmitter<ManualUiTreeNodeStatusModel>();

    setNodeAsSelected(runnerTreeNodeModel: ManualUiTreeNodeStatusModel) {
        this.selectedRunnerTreeNode = runnerTreeNodeModel;

        this.selectedRunnerTreeNodeObserver.emit(runnerTreeNodeModel);
    }
}
