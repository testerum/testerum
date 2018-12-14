import {Path} from "../../path";

export class Arg {

    constructor(public readonly name: string|null,
                public readonly content: string|null,
                public readonly type: string,
                public readonly path: Path|null,
                public readonly oldPath: Path|null) {}

    static parse(input: Object): Arg {
        const name = input["name"];
        const content = input["content"];
        const type = input["type"];
        const path = Path.parse(input["path"]);
        const oldPath = Path.parse(input["oldPath"]);

        return new Arg(name, content, type, path, oldPath);
    }
}
