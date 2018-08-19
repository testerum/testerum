import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEvent} from "./runner.event";
import {StepCall} from "../../step-call.model";
import {EventKey} from "./fields/event-key.model";
import {ExceptionDetail} from "./fields/exception-detail.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class StepEndEvent implements RunnerEvent, Serializable<StepEndEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.STEP_END_EVENT;

    stepCall: StepCall;
    status: ExecutionStatusEnum;
    exceptionDetail: ExceptionDetail;
    exceptionDetailAsString: string;
    durationMillis: number;

    deserialize(input: Object): StepEndEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);

        this.stepCall = new StepCall().deserialize(input["stepCall"]);

        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        if (input["exceptionDetail"]) {
            this.exceptionDetail = new ExceptionDetail().deserialize(input["exceptionDetail"]);
        }
        this.exceptionDetailAsString = input["exceptionDetailAsString"];
        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
