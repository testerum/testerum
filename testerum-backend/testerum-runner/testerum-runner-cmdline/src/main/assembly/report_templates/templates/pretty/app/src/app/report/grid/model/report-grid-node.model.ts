import {TreeNode} from "primeng/api";
import {ReportGridNodeData} from "./report-grid-node-data.model";

export class ReportGridNode implements TreeNode{
    parent: ReportGridNode;
    children: TreeNode[] = [];
    expanded: boolean;
    leaf: boolean;
    data: ReportGridNodeData;
}
