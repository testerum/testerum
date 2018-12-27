import {ExceptionStackTraceElementDetail} from "./exception-stack-trace-element-detail.model";
import {Serializable} from "../../../infrastructure/serializable.model";

export class ExceptionDetail implements Serializable<ExceptionDetail> {

    exceptionClassName: string;
    message: string;
    stackTrace: Array<ExceptionStackTraceElementDetail> = [];
    cause: ExceptionDetail;
    suppressed: Array<ExceptionDetail> = [];

    asString: string;
    asDetailedString: string;

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

        this.asString = input["asString"];
        this.asDetailedString = input["asDetailedString"];

        return this;
    }

    serialize(): string {
        return "";
    }
}
