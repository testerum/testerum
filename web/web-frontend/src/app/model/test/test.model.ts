import {TreeNodeModel} from "../infrastructure/tree-node.model";
import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {StepCall} from "../step-call.model";
import {Path} from "../infrastructure/path/path.model";
import {TestProperties} from "./test-properties.model";
import {Warning} from "../warning/Warning";
import {Serializable} from "../infrastructure/serializable.model";

export class TestModel implements Serializable<TestModel>, TreeNodeModel {

    id:string = IdUtils.getTemporaryId();
    path:Path;
    properties: TestProperties  = new TestProperties();
    text:string;
    description:string;
    tags: Array<string> = [];

    stepCalls:Array<StepCall> = [];

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    deserialize(input: Object): TestModel {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.properties = new TestProperties().deserialize(input['properties']);
        this.text = input['text'];
        this.description = input['description'];
        this.tags = input['tags'] || [];

        this.stepCalls = [];
        for (let stepCall of (input['stepCalls'] || [])) {
            this.stepCalls.push(
                new StepCall().deserialize(stepCall)
            );
        }

        this.warnings = [];
        for (let warning of (input['warnings'] || [])) {
            this.warnings.push(
                new Warning().deserialize(warning)
            );
        }

        this.descendantsHaveWarnings = input['descendantsHaveWarnings'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"id":' + JsonUtil.stringify(this.id) + ',' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"properties":' + JsonUtil.serializeSerializable(this.properties) + ',' +
            '"text":' + JsonUtil.stringify(this.text) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.stringify(this.tags) + ',' +
            '"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) + ',' +
            '"warnings": []' +
            '}'
    }

}
