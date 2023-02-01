
import {TreeNodeModel} from "../../../../../../../model/infrastructure/tree-node.model";
import {RdbmsField} from "../../../../../../../model/resource/rdbms/schema/rdbms-field.model";
import {NodeState} from "../enum/node-state.enum";
import {JsonUtil} from "../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../model/infrastructure/serializable.model";
import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";

export class FieldVerify extends JsonTreeNodeAbstract implements Serializable<FieldVerify> {
    id: string; //unused, some refactoring need it

    name: string;

    value: any;
    nodeState:NodeState;

    constructor(parentContainer: JsonTreeContainer, name: string) {
        super(parentContainer);
        this.name = name;
    }

    static createInstanceFromFieldVerifyModel(parentContainer: JsonTreeContainer, field: FieldVerify) {
        let instance = new FieldVerify(parentContainer, field.name);
        instance.value = field.value;

        instance.nodeState = NodeState.USED;
        return instance;
    }

    static createInstanceFromRdbmsField(parentContainer: JsonTreeContainer, rdbmsField: RdbmsField, nodeState:NodeState) {
        let instance = new FieldVerify(parentContainer, rdbmsField.name);
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
