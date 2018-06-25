
import {TestsRunnerLogLineModel} from "./tests-runner-log-line.model";
import {LogLineTypeEnum} from "./log-line-type.enum";
import {RunnerTreeNodeModel} from "../../tests-runner-tree/model/runner-tree-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {RunnerEventTypeEnum} from "../../../../../model/test/event/enums/runner-event-type.enum";
export class TestsRunnerLogModel {

    eventKey: EventKey;
    eventType: RunnerEventTypeEnum;
    time: Date;
    textLines: Array<TestsRunnerLogLineModel> = [];

    addLogLine(message:string) {
        this.textLines.push(
            new TestsRunnerLogLineModel(message, LogLineTypeEnum.LOG)
        )
    }
    addExceptionLine(message:string) {
        this.textLines.push(
            new TestsRunnerLogLineModel(message, LogLineTypeEnum.EXCEPTION)
        )
    }
    addStepLine(message:string) {
        this.textLines.push(
            new TestsRunnerLogLineModel(message, LogLineTypeEnum.STEP)
        )
    }
}
