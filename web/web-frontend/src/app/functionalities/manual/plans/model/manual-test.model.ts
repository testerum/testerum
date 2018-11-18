import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTestStatus} from "./enums/manual-test-status.enum";
import {ManualTestStepStatus} from "./enums/manual-test-step-status.enum";
import {StepCall} from "../../../../model/step-call.model";

export class ManualTest implements Serializable<ManualTest>{

    path: Path;
    oldPath: Path;
    name: string;
    description: string;
    tags:Array<string> = [];

    stepsStatus: ManualTestStepStatus[] = [];
    stepCalls:Array<StepCall> = [];

    status: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;
    comments: string;

    isFinalized: boolean;

    deserialize(input: Object): ManualTest {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.name = input['name'];
        this.description = input['description'];

        for (let tag of (input['tags']) || []) {
            this.tags.push(tag);
        }

        for (const stepStatus of (input['stepsStatus']) || []) {
            this.stepsStatus.push(ManualTestStepStatus.fromString(stepStatus));
        }

        for (let stepCall of (input['stepCalls'] || [])) {
            this.stepCalls.push(
                new StepCall().deserialize(stepCall)
            );
        }

        this.status = ManualTestStepStatus.fromString(input['status']);

        this.comments = input['comments'];
        this.isFinalized = input['isFinalized'];
        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) +
            ',"name":' + JsonUtil.stringify(this.name) +
            ',"description":' + JsonUtil.stringify(this.description) +
            ',"tags":' + JsonUtil.serializeArray(this.tags) +
            ',"stepsStatus":' + JsonUtil.serializeArrayOfSerializable(this.stepsStatus) +
            ',"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) +
            ',"status":' + JsonUtil.stringify(this.status.toString()) +
            ',"comments":' + JsonUtil.stringify(this.comments) +
            ',"isFinalized":' + JsonUtil.stringify(this.isFinalized) +
            '}'
    }
}
