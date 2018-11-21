import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTestStatus} from "./enums/manual-test-status.enum";
import {ManualStepCall} from "./manual-step-call.model";

export class ManualTest implements Serializable<ManualTest>{

    path: Path;
    oldPath: Path;
    name: string;
    description: string;
    tags:Array<string> = [];

    stepCalls:Array<ManualStepCall> = [];

    status: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;
    comments: string;

    finalized: boolean;

    deserialize(input: Object): ManualTest {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.name = input['name'];
        this.description = input['description'];

        for (let tag of (input['tags']) || []) {
            this.tags.push(tag);
        }

        for (let stepCall of (input['stepCalls'] || [])) {
            this.stepCalls.push(
                new ManualStepCall().deserialize(stepCall)
            );
        }

        this.comments = input['comments'];
        this.finalized = input['finalized'];

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
            ',"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) +
            ',"status":' + JsonUtil.stringify(this.status.toString()) +
            ',"comments":' + JsonUtil.stringify(this.comments) +
            ',"finalized":' + JsonUtil.stringify(this.finalized) +
            '}'
    }
}
