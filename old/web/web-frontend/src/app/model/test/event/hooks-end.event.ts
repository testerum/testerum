import {RunnerEvent} from "./runner.event";
import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {HookPhase} from "./enums/hook-phase.enum";

export class HooksEndEvent implements RunnerEvent, Serializable<HooksEndEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.HOOKS_END_EVENT;

    hookPhase: HookPhase;

    status: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): HooksEndEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.hookPhase = HookPhase[""+input["hookPhase"]];

        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
