import {RunnerNode} from "./runner-node.model";
import {RunnerContainerNode} from "./runner-container-node.model";
import {Path} from "../../infrastructure/path/path.model";
import {ArrayUtil} from "../../../utils/array.util";
import {RunnerDeserializationUtil} from "./util/runner-deserialization.util";

export class RunnerRootNode implements Serializable<RunnerRootNode>, RunnerContainerNode {
    id: string;
    path: Path;
    name: string;
    children: Array<RunnerNode> = [];

    deserialize(input: Object): RunnerRootNode {
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
