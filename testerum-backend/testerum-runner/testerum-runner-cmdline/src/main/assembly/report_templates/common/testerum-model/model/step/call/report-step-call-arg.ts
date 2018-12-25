import {Path} from "../../path";

export class ReportStepCallArg {

    constructor(public readonly name: string|null,
                public readonly content: string|null,
                public readonly type: string,
                public readonly path: Path|null) {}

    static parse(input: Object): ReportStepCallArg {
        const name = input["name"];
        const content = input["content"];
        const type = input["type"];
        const path = Path.createInstance(input["path"]);

        return new ReportStepCallArg(name, content, type, path);
    }
}
