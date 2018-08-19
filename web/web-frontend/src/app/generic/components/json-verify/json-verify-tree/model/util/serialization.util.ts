import {ArrayJsonVerify} from "../array-json-verify.model";
import {ObjectJsonVerify} from "../object-json-verify.model";
import {StringJsonVerify} from "../string-json-verify.model";
import {BooleanJsonVerify} from "../boolean-json-verify.model";
import {NumberJsonVerify} from "../number-json-verify.model";
import {NullJsonVerify} from "../null-json-verify.model";
import {JsonTreeNode} from "../../../../json-tree/model/json-tree-node.model";
import {JsonTreeNodeSerializable} from "../../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../../json-tree/model/json-tree-container.model";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class SerializationUtil implements Serializable<JsonTreeNode>{


    deserialize(input: Object): JsonTreeNodeSerializable {
        return SerializationUtil.deserialize(input, null);
    }

    serialize(): string {
        throw Error("this method should not be called")
    }

    public static deserialize(input: any, parent: JsonTreeContainer): JsonTreeNodeSerializable {
        let instance:SerializableUnknown<JsonTreeNodeSerializable>;

        //this needs to be before Object verify
        instance = new NullJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new ArrayJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new ObjectJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new StringJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new BooleanJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new NumberJsonVerify(parent);
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        return null;
    }

    public static isPrimitiveType(node: JsonTreeNode): boolean {
        if(node instanceof NullJsonVerify) {
            return true
        }
        if(node instanceof StringJsonVerify) {
            return true
        }
        if(node instanceof BooleanJsonVerify) {
            return true
        }
        if(node instanceof NumberJsonVerify) {
            return true
        }

        return false;
    }
}
