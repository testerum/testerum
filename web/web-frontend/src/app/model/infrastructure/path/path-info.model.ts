import {Serializable} from "../serializable.model";
import {JsonUtil} from "../../../utils/json.util";

export class PathInfo implements Serializable<PathInfo> {

    pathAsString: string;
    isValidPath: boolean = false;
    isExistingPath: boolean = false;
    isProjectDirectory: boolean = false;
    canCreateChild: boolean = true;

    deserialize(input: Object): PathInfo {
        this.pathAsString = input['pathAsString'];
        this.isValidPath = input['validPath'];
        this.isExistingPath = input['existingPath'];
        this.isProjectDirectory = input['projectDirectory'];
        this.canCreateChild = input['canCreateChild'];
        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"pathAsString":' + JsonUtil.stringify(this.pathAsString) +
            ',"isValidPath":' + JsonUtil.stringify(this.isValidPath) +
            ',"isExistingPath":' + JsonUtil.stringify(this.isExistingPath) +
            ',"isProjectDirectory":' + JsonUtil.stringify(this.isProjectDirectory) +
            ',"canCreateChild":' + JsonUtil.stringify(this.canCreateChild) +
            '}'
    }
}
