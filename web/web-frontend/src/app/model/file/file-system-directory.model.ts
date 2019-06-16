import {Serializable} from "../infrastructure/serializable.model";
import {FileSystemFile} from "./file-system-file.model";
import {FileSystemEntry} from "./file-system-entry.model";
import {RunnerTestNode} from "../runner/tree/runner-test-node.model";
import {RunnerFeatureNode} from "../runner/tree/runner-feature-node.model";

export class FileSystemDirectory  implements FileSystemEntry, Serializable<FileSystemDirectory> {
    name: string;
    absoluteJavaPath: string;
    isProject: boolean;
    canCreateChild: boolean;
    hasChildren: boolean;
    children: Array<FileSystemEntry> = [];

    deserialize(input: Object): FileSystemDirectory {
        this.name = input["name"];
        this.absoluteJavaPath = input["absoluteJavaPath"];
        this.isProject = input["isProject"];
        this.canCreateChild = input["canCreateChild"];
        this.hasChildren = input["hasChildren"];
        for (let childDirToDeserialize of input["children"]) {
            if (childDirToDeserialize["@type"] === "FILE") {
                this.children.push(
                    new FileSystemFile().deserialize(childDirToDeserialize)
                );
                continue;
            }
            if (childDirToDeserialize["@type"] === "DIR") {
                this.children.push(
                    new FileSystemDirectory().deserialize(childDirToDeserialize)
                );
                continue;
            }

            throw Error(`unknown node type [${input["@type"]}]`);
        }

        return this;
    }

    serialize(): string {
        throw new Error("not implemented");
    }
}
