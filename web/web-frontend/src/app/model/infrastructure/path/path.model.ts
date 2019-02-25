import {JsonUtil} from "../../../utils/json.util";
import {StringUtils} from "../../../utils/string-utils.util";
import {ArrayUtil} from "../../../utils/array.util";
import {Serializable} from "../serializable.model";

export class Path implements Serializable<Path> {
    readonly directories: Array<string> = [];
    readonly fileName: string;
    readonly fileExtension: string;

    constructor(directories: Array<string>, fileName: string, fileExtension: string) {
        this.directories = directories;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
    }

    static deserialize(input: Object): Path {
        return new Path([], null, null).deserialize(input);
    }

    static createInstance(pathAsString: string): Path {

        if (!pathAsString) {
            return Path.createInstanceOfEmptyPath();
        }

        let pathsPart = pathAsString.split("/").filter(value => !StringUtils.isEmpty(value));

        let fileName:string = null;
        let extension:string = null;

        let dotIndexInFileName = 0;
        if (pathsPart.length > 0) {
            let lastPathPart = pathsPart[pathsPart.length - 1];
            dotIndexInFileName = lastPathPart.search('\\.');

            if (dotIndexInFileName > 0) {
                pathsPart.splice(pathsPart.length - 1, 1);
                fileName = lastPathPart.substring(0, dotIndexInFileName);
                extension = lastPathPart.substring(dotIndexInFileName + 1, lastPathPart.length)
            }
        }
        return new Path(pathsPart, fileName, extension)
    }
    static createInstanceFromPath(path: Path): Path {

        if(!path) {
            throw new Error("Can't create a Path instance from null")
        }
        return new Path(
            ArrayUtil.copyArray(path.directories),
            path.fileName,
            path.fileExtension
        );
    }

    static createInstanceOfEmptyPath() {
        return new Path([], null, null)
    }

    getLastPathPart(): string {
        if(this.directories.length == 0) {
            return null;
        }
        return this.directories[this.directories.length - 1]
    }

    hasPrefix(path: Path): boolean {
        for (let i = 0; i < path.directories.length; i++) {
            let prefixDir = path.directories[i];
            if(!this.directories[i] || prefixDir != this.directories[i]) {
                return false;
            }
        }

        if(path.fileName && path.fileName != this.fileName) {
            return false;
        }

        if(path.fileExtension && path.fileExtension != this.fileExtension) {
            return false;
        }

        return true;
    }

    addDirectory(dir: string): Path {
        let newDirectories = ArrayUtil.copyArray(this.directories);
        newDirectories.push(dir);

        return new Path(newDirectories, this.fileName, this.fileExtension)
    }

    minusPrefix(path: Path): Path {
        if(path.fileName || path.fileExtension) {
            throw new Error("Invalid Root Path");
        }

        let resultDirectories = ArrayUtil.copyArray(this.directories);
        for (let rootPathDirectory of path.directories) {
            if(rootPathDirectory == resultDirectories[0]) {
                resultDirectories.shift()
            } else {
                throw Error("["+path.toString()+"] is not a prefix of ["+this.toString()+"]")
            }
        }

        return new Path(resultDirectories, this.fileName, this.fileExtension)
    }

    deserialize(input: Object): Path {
        if (!input) {
            return null;
        }

        let directories: Array<string> = [];
        for (let directory of input["directories"]) {
            directories.push(directory);
        }

        let fileName = input["fileName"];
        let fileExtension = input["fileExtension"];

        return new Path(directories, fileName, fileExtension);
    }

    serialize() {
        return "" +
            '{' +
            '"directories":' + JsonUtil.serializeArray(this.directories) + ',' +
            '"fileName":' + JsonUtil.stringify(this.fileName) + ',' +
            '"fileExtension":' + JsonUtil.stringify(this.fileExtension) +
            '}'
    }

    toDirectoryString(absolutePath: boolean = false): string {
        let result: string = this.directories.join('/');

        if (!result.startsWith('/') && !absolutePath) {
            result = '/' + result;
        }
        if (!result.endsWith('/')) {
            result = result + '/';
        }

        return result
    }

    isFile(): boolean {
        return !StringUtils.isEmpty(this.fileName)
    }

    isEmpty(): boolean {
        return this.directories.length == 0 && this.fileName == null && this.fileExtension == null;
    }

    getParentPath():Path {
        if (this.isFile()) {
            return new Path(this.directories, null, null);
        }
        if (this.directories.length == 0) {
            return Path.createInstanceOfEmptyPath();
        }

        let directories = ArrayUtil.copyArray(this.directories);
        directories.pop();
        return new Path(directories, null, null)
    }

    withoutFileExtension(): Path {
        return new Path(this.directories, this.fileName, null);
    }

    toString(absolutePath: boolean = false): string {
        let result = this.toDirectoryString(absolutePath);

        if (this.fileName) {
            result += this.fileName
        }
        if (this.fileExtension != null) {
            result += "." + this.fileExtension
        }

        return result
    }

    equals(path: Path): boolean {
        if(path == null) return false;

        if (this.fileName != path.fileName) return false;
        if (this.fileExtension != path.fileExtension) return false;
        if (this.directories.length != path.directories.length) return false;

        for (let i = 0; i < this.directories.length; i++) {
            if(this.directories[i] != path.directories[i]) return false;
        }

        return true;
    }

}
