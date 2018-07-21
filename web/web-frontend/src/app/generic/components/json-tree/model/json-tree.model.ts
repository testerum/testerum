
import {JsonTreeNode} from "./json-tree-node.model";
import {JsonTreeContainerAbstract} from "./json-tree-container.abstract";
import {T} from "@angular/core/src/render3";
import {ObjectUtil} from "../../../../utils/object.util";
import {JsonTreeContainer} from "./json-tree-container.model";

export class JsonTreeModel extends JsonTreeContainerAbstract {

    children:Array<JsonTreeNode> = [];

    constructor() {
        super(null);
    }

    getChildren(): Array<JsonTreeNode> {
        return this.children;
    }

    getAllTreeNodes<T extends JsonTreeNode>(): Array<T> {
        let result: Array<T> = [];
        for (const child of this.children) {
            this.addChildAndDescendentsToResult(child, result)
        }
        return result;
    }

    private addChildAndDescendentsToResult<T extends JsonTreeNode>(child: T, result: Array<T>) {
        result.push(child);
        if(ObjectUtil.hasAMethodCalled(child, "getChildren")) {
            for (const nephew of child["getChildren"]()) { //ugly, it should be  (child as JsonTreeContainer).getChildren(), but this throws exception
                this.addChildAndDescendentsToResult(nephew, result);
            }
        }
    }

    getAllLeafNodes<T extends JsonTreeNode>(): Array<T> {
        let result: Array<T> = [];
        for (const child of this.children) {
            this.addChildLeafNodesToResult(child, result)
        }
        return result;
    }

    private addChildLeafNodesToResult<T extends JsonTreeNode>(child: T, result: Array<T>) {
        if (!child.isContainer()) {
            result.push(child);
        } else {
            for (const nephew of child["getChildren"]()) { //ugly, it should be  (child as JsonTreeContainer).getChildren(), but this throws exception
                this.addChildLeafNodesToResult(nephew, result);
            }
        }
    }

    refreshContainerVisibilityBasedOnChildVisibility(): void {
        for (const child of this.getChildren()) {
            if (child.isContainer()) {
                this.calculateContainerVisibilityBasedOnChildVisibility(child as JsonTreeContainer);
            }
        }
    }

    private calculateContainerVisibilityBasedOnChildVisibility(parentNode: JsonTreeContainer): void {
        let hasVisibleChild = false;
        for (const child of parentNode.getChildren()) {
            if (child.isContainer()) {
                this.calculateContainerVisibilityBasedOnChildVisibility(child as JsonTreeContainer);
            }

            if (!child.isHidden()) {
                hasVisibleChild = true;
            }
        }

        this.setHidden(!hasVisibleChild);
    }
}
