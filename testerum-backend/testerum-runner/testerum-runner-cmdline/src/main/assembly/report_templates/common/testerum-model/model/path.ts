import {MarshallingUtils} from "../json-marshalling/marshalling-utils";

export class Path {

    constructor(public readonly directories: Array<string>,
                public readonly fileName: string,
                public readonly fileExtension: string) {}

    static parse(input: Object): Path {
        if (!input) {
            return null;
        }

        const directories = MarshallingUtils.parseListOfStrings(input["directories"]);
        let fileName = input["fileName"];
        let fileExtension = input["fileExtension"];

        return new Path(directories, fileName, fileExtension);
    }

}
