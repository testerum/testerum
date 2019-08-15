import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {StepCall} from "../../../../model/step-call.model";
import {ManualTestStepStatus} from "./enums/manual-test-step-status.enum";
import {JsonUtil} from "../../../../utils/json.util";

export class ManualStepCall implements Serializable<ManualStepCall> {

    stepCall: StepCall;
    status: ManualTestStepStatus;
    enabled: boolean = true;

    deserialize(input: Object): ManualStepCall {
        this.stepCall = new StepCall().deserialize(input["stepCall"]);
        this.status = ManualTestStepStatus.fromString(input["status"]);
        this.enabled = input['enabled'];

        return this;

    }

    serialize(): string {
        return "" +
            '{' +
            '"stepCall":' + JsonUtil.serializeSerializable(this.stepCall) +
            ',"status":' + JsonUtil.serializeSerializable(this.status) +
            ',"enabled":' + JsonUtil.stringify(this.enabled) +
            '}'
    }

    clone(): ManualStepCall {
        return new ManualStepCall().deserialize(JSON.parse(this.serialize()));
    }

}
