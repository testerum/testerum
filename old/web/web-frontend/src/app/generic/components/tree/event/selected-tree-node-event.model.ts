
import {TreeNodeModel} from "../../../../model/infrastructure/tree-node.model";

export class SelectedTreeNodeEventModel {
    treeId:string;
    treeNodeModel:TreeNodeModel;

    constructor(treeId:string, treeNodeModel:TreeNodeModel) {
        this.treeId = treeId;
        this.treeNodeModel = treeNodeModel;
    }
}