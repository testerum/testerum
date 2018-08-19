import { RunnerEvent } from "./runner.event";
import { EventKey } from "./fields/event-key.model";
import { ExecutionStatusEnum } from "./enums/execution-status.enum";
import { ExceptionDetail } from "./fields/exception-detail.model";
import { RunnerEventTypeEnum } from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class FeatureEndEvent implements RunnerEvent, Serializable<FeatureEndEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.FEATURE_END_EVENT;

    featureName: string;

    status: ExecutionStatusEnum;
    exceptionDetail: ExceptionDetail;
    exceptionDetailAsString: string;
    durationMillis: number;

    deserialize(input: Object): FeatureEndEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.featureName = input["featureName"];

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
