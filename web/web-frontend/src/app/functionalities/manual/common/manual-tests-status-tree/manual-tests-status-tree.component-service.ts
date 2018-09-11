import {EventEmitter, Injectable} from "@angular/core";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ManualUiTreeNodeStatusModel} from "./model/manual-ui-tree-node-status.model";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ManualUiTreeBaseStatusModel} from "./model/manual-ui-tree-base-status.model";
import {ManualUiTreeRootStatusModel} from "./model/manual-ui-tree-root-status.model";

@Injectable()
export class ManualTestsStatusTreeComponentService {

    treeModel: JsonTreeModel;
    treeRootNode: ManualUiTreeRootStatusModel;
    treeTestsNodes: ManualUiTreeNodeStatusModel[] = [];
    treeTestsWithFoldersNodes: ManualUiTreeBaseStatusModel[] = [];

    selectedRunnerTreeNode: ManualUiTreeNodeStatusModel;
    selectedRunnerTreeNodeObserver: EventEmitter<ManualUiTreeNodeStatusModel> = new EventEmitter<ManualUiTreeNodeStatusModel>();

    setNodeAsSelected(runnerTreeNodeModel: ManualUiTreeNodeStatusModel) {
        this.selectedRunnerTreeNode = runnerTreeNodeModel;

        this.selectedRunnerTreeNodeObserver.emit(runnerTreeNodeModel);
    }

    showTestFolders(showTestFolders: boolean) {
        if (showTestFolders) {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsWithFoldersNodes);
        } else {
            ArrayUtil.replaceElementsInArray(this.treeRootNode.children, this.treeTestsNodes);
        }
    }
}
