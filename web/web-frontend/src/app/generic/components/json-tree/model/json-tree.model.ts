
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
}
