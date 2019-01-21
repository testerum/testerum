import {Serializable} from "../infrastructure/serializable.model";

export class FileSystemDirectory implements Serializable<FileSystemDirectory> {
    name: string;
    absoluteJavaPath: string;
    isProject: boolean;
    canCreateChild: boolean;
    hasChildrenDirectories: boolean;
    childrenDirectories: Array<FileSystemDirectory> = [];

    deserialize(input: Object): FileSystemDirectory {
        this.name = input["name"];
        this.absoluteJavaPath = input["absoluteJavaPath"];
        this.isProject = input["isProject"];
        this.canCreateChild = input["canCreateChild"];
        this.hasChildrenDirectories = input["hasChildrenDirectories"];
        for (let childDirToDeserialize of input["childrenDirectories"]) {
            this.childrenDirectories.push(
                new FileSystemDirectory().deserialize(childDirToDeserialize)
            )
        }

        return this;
    }

    serialize(): string {
        throw new Error("not implemented");
    }
}
