import {LogLevel} from "./log-level";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";
import {ExceptionDetail} from "../exception/exception-detail";

export class ReportLog {

    constructor(public readonly time: Date,
                public readonly logLevel: LogLevel,
                public readonly message: string,
                public readonly exceptionDetail: ExceptionDetail | null) { }

    static parse(input: Object): ReportLog {
        if (!input) {
            return null;
        }

        const time = MarshallingUtils.parseLocalDateTime(input["time"]);
        const logLevel = MarshallingUtils.parseEnum(input["logLevel"], LogLevel);
        const message = input["message"];
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);

        return new ReportLog(time, logLevel, message, exceptionDetail);
    }

    getMessageWithException(): string {
        let result = "";

        result += this.message;

        if (this.exceptionDetail != null) {
            result += "; exception:\n";
            result += this.exceptionDetail.asDetailedString;
        }

        return result;
    }

}
