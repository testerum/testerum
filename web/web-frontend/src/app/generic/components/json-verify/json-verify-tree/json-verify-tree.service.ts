import {Injectable} from "@angular/core";
import {EmptyJsonVerify} from "./model/empty-json-verify.model";
import {JsonIntegrity} from "./model/infrastructure/json-integrity.interface";
import {JsonTreeNode} from "../../json-tree/model/json-tree-node.model";
import {JsonTreeModelSerializable} from "../../json-tree/model/serializable/json-tree-serializable.model";
import {JsonTreeNodeSerializable} from "../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {JsonTreeContainerAbstract} from "../../json-tree/model/json-tree-container.abstract";

@Injectable()
export class JsonVerifyTreeService {

    jsonSchema: JsonTreeNode;
    rootNode: JsonTreeModelSerializable = new JsonTreeModelSerializable();
    editMode: boolean = false;

    constructor() {
        this.rootNode.children.push(new EmptyJsonVerify(this.rootNode))
    }

    setEmpty() {
        this.jsonSchema = null;
        this.rootNode = new JsonTreeModelSerializable();
        this.rootNode.children.push(new EmptyJsonVerify(this.rootNode));
    }

    setJsonVerifyRootResource(rootNode: JsonTreeNodeSerializable) {
        rootNode.parentContainer = this.rootNode;
        this.rootNode.children.length = 0;
        this.rootNode.children.push(rootNode);
    }

    replaceNode(nodeToReplace: JsonTreeNode, newNode: JsonTreeNode) {
        ArrayUtil.replaceElementInArray(nodeToReplace.getParent().getChildren(), nodeToReplace, newNode);
    }

    deleteEntry(node: JsonTreeNode) {
        this.deleteNode(this.rootNode.children, node);

        if(this.rootNode.children.length == 0) {
            this.rootNode.children.push(new EmptyJsonVerify(this.rootNode));
        }
    }
    private deleteNode(nodes: Array<JsonTreeNode>, nodeToDelete: JsonTreeNode):boolean {
        for (let node of nodes) {
            if(node === nodeToDelete) {
                ArrayUtil.removeElementFromArray(nodes, nodeToDelete);
                return true;
            }

            if(node.isContainer()) {
                this.deleteNode((node as JsonTreeContainerAbstract).getChildren(), nodeToDelete);
            }
        }

        return false;
    }

    empty() {
        this.rootNode.children.length = 0;
    }

    isModelValid(): boolean {
        return this.areChildrenValid(this.rootNode.children)
    }

    private areChildrenValid(children: Array<JsonTreeNode>): boolean {
        for (let child of children) {
            let jsonIntegrity:JsonIntegrity = child as any as JsonIntegrity;
            if(!jsonIntegrity.isValid()) {
                return false
            }

            if(child instanceof JsonTreeContainerAbstract) {
                let areChildrenValid = this.areChildrenValid((child as JsonTreeContainerAbstract).getChildren());
                if(!areChildrenValid){
                    return areChildrenValid;
                }
            }
        }
        return true
    }

    removeEmptyNodes() {
        let emptyNodes = this.getEmptyNodes(this.rootNode.children);
        for (let emptyNode of emptyNodes) {
            this.deleteNode(this.rootNode.children, emptyNode);
        }
    }

    private getEmptyNodes(nodes: Array<JsonTreeNode>): Array<JsonTreeNode> {
        let emptyNodes:Array<JsonTreeNode> = [];
        for (let node of nodes) {
            let jsonIntegrity:JsonIntegrity = node as any as JsonIntegrity;
            if(jsonIntegrity.isEmptyAndShouldNotBeSaved()) {
                emptyNodes.push(node)
            }

            if(node instanceof JsonTreeContainerAbstract) {
                let childrenEmtpyNodes = this.getEmptyNodes((node as JsonTreeContainerAbstract).getChildren());
                emptyNodes = emptyNodes.concat(childrenEmtpyNodes)
            }
        }
        return emptyNodes;
    }

    setJsonSchema(jsonSchema: JsonTreeNodeSerializable) {
        this.jsonSchema = jsonSchema;
        let rootNodes = this.rootNode.children;
        if(this.isEmptyModel()) {
            rootNodes.length = 0;
            rootNodes.push(jsonSchema)
        }
    }

    isEmptyModel() {

        let rootNodes = this.rootNode.children;
        return rootNodes.length == 0
            || (rootNodes.length == 1 && rootNodes[0] instanceof EmptyJsonVerify)
    }
}
