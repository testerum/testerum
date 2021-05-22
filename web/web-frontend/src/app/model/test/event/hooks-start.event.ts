import {RunnerEvent} from "./runner.event";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {Scenario} from "../scenario/scenario.model";
import {StringUtils} from "../../../utils/string-utils.util";
import {ObjectUtil} from "../../../utils/object.util";
import {HookPhase} from "./enums/hook-phase.enum";

export class HooksStartEvent implements RunnerEvent, Serializable<HooksStartEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.HOOKS_START_EVENT;

    hookPhase: HookPhase;

    deserialize(input: Object): HooksStartEvent {
        this.time = new Date(input["time"]);
        this.eventKey = input["eventKey"];
        this.hookPhase = HookPhase[""+input["hookPhase"]];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
