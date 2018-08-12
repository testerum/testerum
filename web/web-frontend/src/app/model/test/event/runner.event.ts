import { EventKey } from "./fields/event-key.model";
import { RunnerEventTypeEnum } from "./enums/runner-event-type.enum";
import { SuiteStartEvent } from "./suite-start.event";
import { SuiteEndEvent } from "./suite-end.event";
import { TestStartEvent } from "./test-start.event";
import { TestEndEvent } from "./test-end.event";
import { StepStartEvent } from "./step-start.event";
import { StepEndEvent } from "./step-end.event";
import { RunnerErrorEvent } from "./runner-error.event";
import { TextLogEvent } from "./text-log.event";
import { FeatureStartEvent } from "./feature-start.event";
import { FeatureEndEvent } from "./feature-end.event";

export interface RunnerEvent {

    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum;

}

export const RunnerEventMarshaller = {
    deserializeRunnerEvent: (runnerEventAsJson: object): RunnerEvent => {
        switch (runnerEventAsJson["@type"]) {
            case "TEST_SUITE_START_EVENT": return new SuiteStartEvent().deserialize(runnerEventAsJson);
            case "TEST_SUITE_END_EVENT"  : return new SuiteEndEvent().deserialize(runnerEventAsJson);
            case "FEATURE_START_EVENT"   : return new FeatureStartEvent().deserialize(runnerEventAsJson);
            case "FEATURE_END_EVENT"     : return new FeatureEndEvent().deserialize(runnerEventAsJson);
            case "TEST_START_EVENT"      : return new TestStartEvent().deserialize(runnerEventAsJson);
            case "TEST_END_EVENT"        : return new TestEndEvent().deserialize(runnerEventAsJson);
            case "STEP_START_EVENT"      : return new StepStartEvent().deserialize(runnerEventAsJson);
            case "STEP_END_EVENT"        : return new StepEndEvent().deserialize(runnerEventAsJson);
            case "ERROR_EVENT"           : return new RunnerErrorEvent().deserialize(runnerEventAsJson);
            case "LOG_EVENT"             : return new TextLogEvent().deserialize(runnerEventAsJson);
        }

        return null;
    }
};
