import {Serializable} from "../infrastructure/serializable.model";
import {JsonUtil} from "../../utils/json.util";

export class CreateFileSystemDirectoryRequest implements Serializable<CreateFileSystemDirectoryRequest> {
    parentAbsoluteJavaPath: string;
    name: string;

    constructor(parentAbsoluteJavaPath: string, name: string) {
        this.parentAbsoluteJavaPath = parentAbsoluteJavaPath;
        this.name = name;
    }

    deserialize(input: Object): CreateFileSystemDirectoryRequest {
        this.parentAbsoluteJavaPath = input["parentAbsoluteJavaPath"];
        this.name = input["name"];

        return this;
    }

    serialize(): string {
        return '' +
            '{' +
                '"parentAbsoluteJavaPath":' + JsonUtil.stringify(this.parentAbsoluteJavaPath) +
                ',"name":' + JsonUtil.stringify(this.name) +
            '}';
    }
}
