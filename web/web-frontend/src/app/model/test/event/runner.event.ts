import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";

export interface RunnerEvent {

    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum;

}

