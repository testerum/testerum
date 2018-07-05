
import {RunnerTreeNodeModel} from "../model/runner-tree-node.model";


export interface RunnerTreeNodeSelectedListener {

    onRunnerTreeNodeSelected( runnerTreeNode: RunnerTreeNodeModel): void;
}
