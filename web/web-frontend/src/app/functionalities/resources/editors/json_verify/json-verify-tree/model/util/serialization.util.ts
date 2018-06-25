
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {SerializableUnknown} from "../../../../../../../model/infrastructure/serializable-unknown.model";
import {ArrayJsonVerify} from "../array-json-verify.model";
import {ObjectJsonVerify} from "../object-json-verify.model";
import {StringJsonVerify} from "../string-json-verify.model";
import {BooleanJsonVerify} from "../boolean-json-verify.model";
import {NumberJsonVerify} from "../number-json-verify.model";
import {NullJsonVerify} from "../null-json-verify.model";
import {JsonTreeNodeSerializable} from "../../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";

export class SerializationUtil implements Serializable<JsonTreeNode>{


    deserialize(input: Object): JsonTreeNodeSerializable {
        return SerializationUtil.deserialize(input);
    }

    serialize(): string {
        throw Error("this method should not be called")
    }

    public static deserialize(input: any): JsonTreeNodeSerializable {
        let instance:SerializableUnknown<JsonTreeNodeSerializable>;

        //this needs to be before Object verify
        instance = new NullJsonVerify();
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new ArrayJsonVerify();
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new ObjectJsonVerify();
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new StringJsonVerify();
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new BooleanJsonVerify();
        if(instance.canDeserialize(input)) {
            return instance.deserialize(input);
        }

        instance = new NumberJsonVerify();
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
