import {EventEmitter, Injectable} from "@angular/core";
import {RunnerConfigTestTreeNodeModel} from "./model/runner-config-test-tree-node.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";

@Injectable()
export class RunnerConfigTestTreeComponentService {

    selectedRunnerTreeNode: RunnerConfigTestTreeNodeModel;

    setNodeAsSelected(runnerTreeNodeModel: RunnerConfigTestTreeNodeModel) {
        this.selectedRunnerTreeNode = runnerTreeNodeModel;
    }
}
