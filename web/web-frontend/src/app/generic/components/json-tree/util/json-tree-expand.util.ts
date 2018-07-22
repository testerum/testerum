import {JsonTreeNode} from "../model/json-tree-node.model";
import {JsonTreeModel} from "../model/json-tree.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {JsonTreePathNode} from "../model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";

export class JsonTreeExpandUtil {

    static expandTreeToLevel(treeNode: JsonTreeModel, levelToExpand:number) {
        treeNode.jsonTreeNodeState.showChildren = levelToExpand > 0;

        JsonTreeExpandUtil.setIfChildIsExpended(treeNode.children, levelToExpand, 1);
    }

    private static setIfChildIsExpended(levelNodes: Array<JsonTreeNode>, levelToExpand: number, currentLevel: number) {
        let childrenLevel = currentLevel + 1;
        for (const levelNode of levelNodes) {
            if (levelNode.isContainer()) {
                let containerNode: JsonTreeContainer = levelNode as JsonTreeContainer;
                containerNode.getNodeState().showChildren = levelToExpand > currentLevel;
                JsonTreeExpandUtil.setIfChildIsExpended(containerNode.getChildren(), levelToExpand, childrenLevel);
            }
        }
    }

    static expandTreeToPathAndReturnNode(treeNode: JsonTreeModel, path: Path): JsonTreePathNode {

        if(!treeNode) { return null; }
        if(!path) { return null; }

        let allTreeNodes: Array<JsonTreePathNode> = treeNode.getAllTreeNodes<JsonTreePathNode>();

        for (const treeNode of allTreeNodes) {
            if (treeNode.path && treeNode.path.equals(path)) {
                this.expandTreeToNode(treeNode);
                return treeNode;
            }
        }
        return null;
    }

    static expandTreeToNode(treeNode: JsonTreePathNode) {
        let parentContainer = treeNode.parentContainer;
        while (parentContainer != null) {
            parentContainer.getNodeState().showChildren = true;
            parentContainer = parentContainer.getParent();
        }
    }
}
