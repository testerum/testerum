export class Path {

    constructor(public readonly directories: Array<string>,
                public readonly fileName: string,
                public readonly fileExtension: string) {}

    static createInstance(pathAsString: string): Path {
        if (!pathAsString) {
            return Path.createInstanceOfEmptyPath();
        }

        let pathsPart = pathAsString.split("/").filter(value => !Path.stringIsEmpty(value));

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

    static createInstanceOfEmptyPath() {
        return new Path([], null, null)
    }

    private static stringIsEmpty(str: string): boolean {
        if (!str) {
            return true;
        }

        if (str.trim && str.trim() === "") {
            return true;
        }

        return false;
    }

    toDirectoryString(): string {
        let result: string = this.directories.join('/');

        if (!result.startsWith('/')) {
            result = '/' + result;
        }
        if (!result.endsWith('/')) {
            result = result + '/';
        }

        return result
    }

    toString(): string {
        let result = this.toDirectoryString();

        if (this.fileName) {
            result += this.fileName
        }
        if (this.fileExtension != null) {
            result += "." + this.fileExtension
        }

        return result
    }

}
