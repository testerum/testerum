import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {TreeNodeModel} from "../../model/infrastructure/tree-node.model";
import {Path} from "../../model/infrastructure/path/path.model";
import {ManualTestStepModel} from "./manual-step.model";

export class ManualTestModel implements Serializable<ManualTestModel>, TreeNodeModel {

    id:string = IdUtils.getTemporaryId();
    path:Path;
    oldPath:Path;
    text:string;
    description:string;
    tags:Array<string> = [];
    steps:Array<ManualTestStepModel> = [];

    deserialize(input: Object): ManualTestModel {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.text = input['text'];
        this.description = input['description'];

        for (let tag of (input['tags']) || []) {
            this.tags.push(tag);
        }

        for (let step of (input['steps']) || []) {
            this.steps.push(new ManualTestStepModel().deserialize(step));
        }

        return this;
    }

    serialize() {
        let response = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"text":' + JsonUtil.stringify(this.text) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.serializeArray(this.tags) + ',' +
            '"steps":' + JsonUtil.serializeArrayOfSerializable(this.steps);

        response += '}';
        return response;
    }
}
