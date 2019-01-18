export class ReportStepCallArg {

    constructor(public readonly name: string|null,
                public readonly content: string|null,
                public readonly type: string,
                public readonly path: string|null) {}

    static parse(input: Object): ReportStepCallArg {
        const name = input["name"];
        const content = input["content"];
        const type = input["type"];
        const path = input["path"];

        return new ReportStepCallArg(name, content, type, path);
    }
}
