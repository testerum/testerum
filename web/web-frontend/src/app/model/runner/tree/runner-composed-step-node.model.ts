import {RunnerNode} from "./runner-node.model";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerContainerNode} from "./runner-container-node.model";
import {RunnerTestNode} from "./runner-test-node.model";
import {ArrayUtil} from "../../../utils/array.util";
import {RunnerDeserializationUtil} from "./util/runner-deserialization.util";

export class RunnerComposedStepNode implements Serializable<RunnerComposedStepNode>, RunnerContainerNode {
    id: string;
    name: string;
    path: Path;
    children: Array<RunnerNode> = [];

    deserialize(input: Object): RunnerComposedStepNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        ArrayUtil.replaceElementsInArray(
            this.children,
            RunnerDeserializationUtil.deserialize(input["children"])
        );

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
