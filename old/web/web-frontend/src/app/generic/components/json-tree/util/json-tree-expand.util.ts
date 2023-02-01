import {JsonTreeNode} from "../model/json-tree-node.model";
import {JsonTreeModel} from "../model/json-tree.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {JsonTreePathNode} from "../model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {RunnerComposedStepTreeNodeModel} from "../../../../functionalities/features/tests-runner/tests-runner-tree/model/runner-composed-step-tree-node.model";

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

    static expandNode(nodeToExpand: JsonTreeNode) {
        if (nodeToExpand.isContainer()) {
            let containerToExpand = nodeToExpand as JsonTreeContainer;
            containerToExpand.getNodeState().showChildren = true;

            for (const child of containerToExpand.getChildren()) {
                JsonTreeExpandUtil.expandNode(child);
            }
        }
    }

    static collapseNode(nodeToExpand: JsonTreeNode) {
        if (nodeToExpand.isContainer()) {
            let containerToExpand = nodeToExpand as JsonTreeContainer;
            containerToExpand.getNodeState().showChildren = false;

            for (const child of containerToExpand.getChildren()) {
                JsonTreeExpandUtil.collapseNode(child);
            }
        }
    }

    static expandTreeToNodeType(treeContainer: JsonTreeContainer, classOfJsonTreeNode: any) {
        if(treeContainer instanceof classOfJsonTreeNode) {
            treeContainer.getNodeState().showChildren = false;
            return;
        }

        for (const childNode of treeContainer.getChildren()) {
            if (childNode.isContainer()) {
                let childContainerNode = childNode as JsonTreeContainer;
                childContainerNode.getNodeState().showChildren = true;
                this.expandTreeToNodeType(childContainerNode, classOfJsonTreeNode);
            }
        }
    }
}
