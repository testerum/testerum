import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {ArrayJsonVerify} from "../json-verify-tree/model/array-json-verify.model";
import {BooleanJsonVerify} from "../json-verify-tree/model/boolean-json-verify.model";
import {EmptyJsonVerify} from "../json-verify-tree/model/empty-json-verify.model";
import {FieldJsonVerify} from "../json-verify-tree/model/field-json-verify.model";
import {NullJsonVerify} from "../json-verify-tree/model/null-json-verify.model";
import {NumberJsonVerify} from "../json-verify-tree/model/number-json-verify.model";
import {ObjectJsonVerify} from "../json-verify-tree/model/object-json-verify.model";
import {StringJsonVerify} from "../json-verify-tree/model/string-json-verify.model";
import {JsonTreeNodeSerializable} from "../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainerSerializable} from "../../../../../generic/components/json-tree/model/serializable/json-tree-container-serializable.model";
import {ArrayUtil} from "../../../../../utils/array.util";

export class JsonSchemaExtractor {

    getJsonSchemaFromJson(jsonRootNode: JsonTreeNodeSerializable): JsonTreeNodeSerializable {
        let nodeSchema = this.getSchemaForNode(jsonRootNode);

        if (jsonRootNode.isContainer()) {
            let nodeSchemaContainer = nodeSchema as JsonTreeContainerSerializable;
            let jsonRootContainer = jsonRootNode as JsonTreeContainerSerializable;

            for (let child of jsonRootContainer.getChildren()) {

                let siblings = nodeSchemaContainer.getChildren();
                let childSchema = this.getJsonSchemaFromJson(child);

                this.aggregateSchema(siblings, childSchema);
            }
        }

        return nodeSchema;
    }

    private getSchemaForNode(node: JsonTreeNodeSerializable): JsonTreeNodeSerializable {
        let result;
        if (node instanceof ArrayJsonVerify) {
            result = new ArrayJsonVerify(null);
        }

        if (node instanceof BooleanJsonVerify) {
            result = new BooleanJsonVerify(null)
        }

        if (node instanceof EmptyJsonVerify) {
            result = new EmptyJsonVerify(null)
        }

        if (node instanceof FieldJsonVerify) {
            result = new FieldJsonVerify(null);
            result.isDirty = false;
            result.name = node.name;

            if(node.isValueAContainer()) {
                let schemaForValue = this.getJsonSchemaFromJson(node.value);
                result.value = schemaForValue;
            } else {
                result.value = null;
            }
        }

        if (node instanceof NullJsonVerify) {
            result = new NullJsonVerify(null)
        }

        if (node instanceof NumberJsonVerify) {
            result = new NumberJsonVerify(null)
        }

        if (node instanceof ObjectJsonVerify) {
            result = new ObjectJsonVerify(null)
        }

        if (node instanceof StringJsonVerify) {
            result = new StringJsonVerify(null)
        }

        return result;
    }

    private aggregateSchema(siblings: Array<JsonTreeNode>, node: JsonTreeNode): void {
        if(siblings.length == 0) { siblings.push(node); return; }

        if(node instanceof BooleanJsonVerify
            || node instanceof EmptyJsonVerify
            || node instanceof NullJsonVerify
            || node instanceof NumberJsonVerify
            || node instanceof StringJsonVerify) {
            return;
        }

        for (let sibling of siblings) {
            if(typeof sibling == typeof node) {
                let aggregatedNode = this.aggregateSchemaOfSameType(sibling, node);

                if (aggregatedNode != null) {
                    ArrayUtil.replaceElementInArray(siblings, sibling, aggregatedNode);
                    return;
                }
            }
        }
        siblings.push(node);
    }

    private aggregateSchemaOfSameType(sibling: JsonTreeNode, node: JsonTreeNode): JsonTreeNode {

        if(node instanceof ArrayJsonVerify) {
            let aggregatedArray = this.aggregateArraySchema(sibling as ArrayJsonVerify, node);
            return aggregatedArray;
        }

        if(node instanceof FieldJsonVerify) {

            if(node.name == (sibling as FieldJsonVerify).name) {
                let aggregatedFiled = this.aggregateFieldSchema(sibling as FieldJsonVerify, node);
                return aggregatedFiled;
            }
        }

        if(node instanceof ObjectJsonVerify) {
            let aggregatedObject = this.aggregateObjectSchema(sibling as ObjectJsonVerify, node);
            return aggregatedObject;
        }

        return null;
    }

    private aggregateObjectSchema(aObject: ObjectJsonVerify, bObject: ObjectJsonVerify): ObjectJsonVerify {
        for (let aChild of aObject.getChildren()) {
            this.aggregateSchema(bObject.getChildren(), aChild);
        }
        return bObject;
    }

    private aggregateFieldSchema(sibling: FieldJsonVerify, node: FieldJsonVerify): FieldJsonVerify {
        if(sibling.name == node.name) {
            return sibling;
        }
        return null;
    }

    private aggregateArraySchema(aArray: ArrayJsonVerify, bArray: ArrayJsonVerify): ArrayJsonVerify {
        for (let aChild of aArray.getChildren()) {
            this.aggregateSchema(bArray.getChildren(), aChild);
        }
        return bArray;
    }
}
