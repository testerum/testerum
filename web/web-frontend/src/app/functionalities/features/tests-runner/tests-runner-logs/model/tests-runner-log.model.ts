import {TestsRunnerLogLineModel} from "./tests-runner-log-line.model";
import {LogLineTypeEnum} from "./log-line-type.enum";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {RunnerEventTypeEnum} from "../../../../../model/test/event/enums/runner-event-type.enum";
import {LogLevel} from "../../../../../model/test/event/enums/log-level.enum";

export class TestsRunnerLogModel {

    eventKey: string;
    eventType: RunnerEventTypeEnum;
    time: Date;
    logLevel: LogLevel = LogLevel.DEBUG;
    textLines: Array<TestsRunnerLogLineModel> = [];

    addLogLine(message:string) {
        this.textLines.push(
            new TestsRunnerLogLineModel(message, LogLineTypeEnum.LOG)
        )
    }
    addException(message: string, stackTrace: string = null) {
        this.textLines.push(
            new TestsRunnerLogLineModel(message, LogLineTypeEnum.EXCEPTION, stackTrace)
        )
    }
}
