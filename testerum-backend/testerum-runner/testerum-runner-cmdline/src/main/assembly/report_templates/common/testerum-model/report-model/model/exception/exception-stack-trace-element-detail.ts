export class ExceptionStackTraceElementDetail {

    constructor(public readonly className: string,
                public readonly methodName: string,
                public readonly fileName: string,
                public readonly lineNumber: number) { }

    static parse(input: Object): ExceptionStackTraceElementDetail {
        if (!input) {
            return null;
        }

        const className = input["className"];
        const methodName = input["methodName"];
        const fileName = input["fileName"];
        const lineNumber = input["lineNumber"];

        return new ExceptionStackTraceElementDetail(className, methodName, fileName, lineNumber);
    }

}
