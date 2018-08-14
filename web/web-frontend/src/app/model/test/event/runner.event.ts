import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";

export interface RunnerEvent {

    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum;

}

