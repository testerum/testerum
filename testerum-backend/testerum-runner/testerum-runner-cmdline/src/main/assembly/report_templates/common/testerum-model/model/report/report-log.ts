import {LogLevel} from "./log-level";
import {MarshallingUtils} from "../../json-marshalling/marshalling-utils";

export class ReportLog {

    constructor(public readonly time: Date,
                public readonly logLevel: LogLevel,
                public readonly message: string) { }

    static parse(input: Object): ReportLog {
        if (!input) {
            return null;
        }

        const time = MarshallingUtils.parseLocalDateTime(input['time']);
        const logLevel = MarshallingUtils.parseEnum(input['logLevel'], LogLevel);
        const message = input['message'];

        return new ReportLog(time, logLevel, message);
    }

}
