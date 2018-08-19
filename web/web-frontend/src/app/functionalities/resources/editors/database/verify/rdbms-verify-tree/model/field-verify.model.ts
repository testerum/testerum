
import {TreeNodeModel} from "../../../../../../../model/infrastructure/tree-node.model";
import {RdbmsField} from "../../../../../../../model/resource/rdbms/schema/rdbms-field.model";
import {NodeState} from "../enum/node-state.enum";
import {JsonUtil} from "../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../model/infrastructure/serializable.model";

export class FieldVerify implements TreeNodeModel, Serializable<FieldVerify> {
    id: string; //unused, some refactoring need it

    name: string;

    value: any;
    nodeState:NodeState;

    constructor(name: string) {
        this.name = name;
    }

    static createInstanceFromFieldVerifyModel(field: FieldVerify) {
        let instance = new FieldVerify(field.name);
        instance.value = field.value;

        instance.nodeState = NodeState.USED;
        return instance;
    }

    static createInstanceFromRdbmsField(rdbmsField: RdbmsField, nodeState:NodeState) {
        let instance = new FieldVerify(rdbmsField.name);
        instance.nodeState = nodeState;

        return instance;
    }

    updateFromRdbmsField(rdbmsField: RdbmsField) {
        this.nodeState = NodeState.USED
    }

    hasError(): boolean {
        return this.nodeState == NodeState.MISSING_FROM_SCHEMA;
    }

    deserialize(input: Object): FieldVerify {
        this.value = input;
        return this;
    }

    serialize(): string {
        return '"'+this.name+'":'+JsonUtil.stringify(this.value);
    }
}
