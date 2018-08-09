import {RunnerNode} from "./runner-node.model";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerContainerNode} from "./runner-container-node.model";

export class RunnerBasicStepNode implements Serializable<RunnerBasicStepNode>, RunnerNode {
    id: string;
    name: string;
    path: Path;

    deserialize(input: Object): RunnerBasicStepNode {
        this.id = input["id"];
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
