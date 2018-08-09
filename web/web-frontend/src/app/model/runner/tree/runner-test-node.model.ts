import {RunnerNode} from "./runner-node.model";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerContainerNode} from "./runner-container-node.model";
import {ArrayUtil} from "../../../utils/array.util";
import {RunnerDeserializationUtil} from "./util/runner-deserialization.util";

export class RunnerTestNode implements Serializable<RunnerTestNode>, RunnerContainerNode {
    id: string;
    name: string;
    path: Path;
    children: Array<RunnerNode> = [];

    deserialize(input: Object): RunnerTestNode {
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
