import {TreeNodeModel} from "../infrastructure/tree-node.model";
import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {StepCall} from "../step-call.model";
import {Path} from "../infrastructure/path/path.model";
import {TestProperties} from "./test-properties.model";

export class TestModel implements Serializable<TestModel>, TreeNodeModel {

    id:string = IdUtils.getTemporaryId();
    properties: TestProperties;
    path:Path;
    text:string;
    description:string;
    tags: Array<string> = [];
    stepCalls:Array<StepCall> = [];

    deserialize(input: Object): TestModel {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.properties = new TestProperties().deserialize(input['properties']);
        this.text = input['text'];
        this.description = input['description'];
        this.tags = input['tags'];

        for (let stepCall of (input['stepCalls']) || []) {
            this.stepCalls.push(new StepCall().deserialize(stepCall));
        }

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
            '"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) +
            '}'
    }
}
