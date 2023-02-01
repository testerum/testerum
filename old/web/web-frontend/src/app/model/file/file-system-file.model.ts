import {Serializable} from "../infrastructure/serializable.model";
import {FileSystemEntry} from "./file-system-entry.model";

export class FileSystemFile implements FileSystemEntry, Serializable<FileSystemFile>{
    name: string;
    absoluteJavaPath: string;

    deserialize(input: Object): FileSystemFile {
        this.name = input["name"];
        this.absoluteJavaPath = input["absoluteJavaPath"];
        return this;
    }

    serialize(): string {
        throw new Error("not implemented");
    }
}
