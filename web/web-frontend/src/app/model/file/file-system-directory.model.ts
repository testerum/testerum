import {Path} from "../infrastructure/path/path.model";

export class FileSystemDirectory implements Serializable<FileSystemDirectory> {
    path: Path;
    hasChildrenDirectories: boolean;
    childrenDirectories: Array<FileSystemDirectory> = [];

    deserialize(input: Object): FileSystemDirectory {
        this.path = Path.deserialize(input["path"]);
        this.hasChildrenDirectories = input["hasChildrenDirectories"];
        for (let childDirToDeserialize of input["childrenDirectories"]) {
            this.childrenDirectories.push(
                new FileSystemDirectory().deserialize(childDirToDeserialize)
            )
        }

        return this;
    }

    serialize(): string {
        return null;
    }
}
