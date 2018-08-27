import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualExecPlan} from "./manual-exec-plan.model";

export class ManualExecPlans implements Serializable<ManualExecPlans>{

    activeExecPlans: Array<ManualExecPlan> = [];
    finalizedExecPlans: Array<ManualExecPlan> = [];

    deserialize(input: Object): ManualExecPlans {
        for (let activeExecPlan of (input['activeExecPlans']) || []) {
            this.activeExecPlans.push(new ManualExecPlan().deserialize(activeExecPlan));
        }

        for (let finalizedExecPlan of (input['finalizedExecPlans']) || []) {
            this.finalizedExecPlans.push(new ManualExecPlan().deserialize(finalizedExecPlan));
        }

        return this;
    }

    serialize(): string {
        let response = "{";

        if (this.activeExecPlans) {
            response += '"activeExecPlans":' + JsonUtil.serializeArrayOfSerializable(this.activeExecPlans);
        }
        if (this.finalizedExecPlans) {
            response += ',"finalizedExecPlans":' + JsonUtil.serializeArrayOfSerializable(this.finalizedExecPlans);
        }
        response += '}';

        return response;
    }
}
