import {ExceptionStackTraceElementDetail} from "./exception-stack-trace-element-detail";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";

export class ExceptionDetail {

    constructor(public readonly exceptionClassName: string,
                public readonly message: string,
                public readonly stackTrace: Array<ExceptionStackTraceElementDetail> = [],
                public readonly cause: ExceptionDetail,
                public readonly suppressed: Array<ExceptionDetail> = [],
                public readonly asString: string,
                public readonly asDetailedString: string) { }

    static parse(input: Object): ExceptionDetail {
        if (!input) {
            return null;
        }

        const exceptionClassName = input["exceptionClassName"];
        const message = input["message"];
        const stackTrace = MarshallingUtils.parseList(input["stackTrace"], ExceptionStackTraceElementDetail);
        const cause = ExceptionDetail.parse(input["cause"]);
        const suppressed = MarshallingUtils.parseList(input["suppressed"], ExceptionDetail);

        const asString = input["asString"];
        const asDetailedString = input["asDetailedString"];

        return new ExceptionDetail(exceptionClassName, message, stackTrace, cause, suppressed, asString, asDetailedString);
    }

}
