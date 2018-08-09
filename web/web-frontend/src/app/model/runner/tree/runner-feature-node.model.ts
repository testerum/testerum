import {RunnerNode} from "./runner-node.model";
import {RunnerContainerNode} from "./runner-container-node.model";
import {ArrayUtil} from "../../../utils/array.util";
import {RunnerDeserializationUtil} from "./util/runner-deserialization.util";
import {Path} from "../../infrastructure/path/path.model";

export class RunnerFeatureNode implements Serializable<RunnerFeatureNode>, RunnerContainerNode {
    id: string;
    name: string;
    path: Path;
    children: Array<RunnerNode> = [];

    deserialize(input: Object): RunnerFeatureNode {
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
