import {LogLevel} from "./log-level";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";
import {ExceptionDetail} from "../exception/exception-detail";

export class ReportLog {

    constructor(public readonly time: Date,
                public readonly logLevel: LogLevel,
                public readonly message: string,
                public readonly exceptionDetail: ExceptionDetail | null) { }

    public static parse(input: Object): ReportLog {
        if (!input) {
            return null;
        }

        const time = MarshallingUtils.parseLocalDateTime(input["time"]);
        const logLevel = MarshallingUtils.parseEnum(input["logLevel"], LogLevel);
        const message = input["message"];
        const exceptionDetail = ExceptionDetail.parse(input["exceptionDetail"]);

        return new ReportLog(time, logLevel, message, exceptionDetail);
    }

    public static getMessageWithException(obj: ReportLog): string {
        let result = "";

        result += obj.message;

        if (obj.exceptionDetail != null) {
            result += "; exception:\n";
            result += obj.exceptionDetail.asDetailedString;
        }

        return result;
    }
}
