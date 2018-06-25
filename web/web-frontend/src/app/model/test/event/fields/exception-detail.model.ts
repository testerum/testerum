import {ExceptionStackTraceElementDetail} from "./exception-stack-trace-element-detail.model";

export class ExceptionDetail implements Serializable<ExceptionDetail> {

    exceptionClassName: string;
    message: string;
    stackTrace: Array<ExceptionStackTraceElementDetail> = [];
    cause: ExceptionDetail;
    suppressed: Array<ExceptionDetail> = [];

    exceptionMessage(): string {
        let result = this.exceptionClassName;

        if (this.message != null) {
            result += ": " + this.message;
        }

        return result;
    }

    //TODO: this is not a finished implementation
    exceptionStackTrace(): string {
        let result = "";
        for (const traceElement of this.stackTrace) {
            result += "\tat " + traceElement.toString() + "\n";
        }

        return result;
    }

    deserialize(input: Object): ExceptionDetail {
        this.exceptionClassName = input["exceptionClassName"];
        this.message = input["message"];

        for (let stackTraceJson of input["stackTrace"]) {
            this.stackTrace.push(new ExceptionStackTraceElementDetail().deserialize(stackTraceJson))
        }

        if (input["cause"]) {
            this.cause = new ExceptionDetail().deserialize(input["cause"]);
        }

        for (let suppressedJson of input["suppressed"]) {
            this.suppressed.push(new ExceptionDetail().deserialize(suppressedJson))
        }

        return this;
    }

    serialize(): string {
        return "";
    }
}
