import {JsonUtil} from "../../../utils/json.util";
import {IdUtils} from "../../../utils/id.util";
import {TreeNodeModel} from "../../../model/infrastructure/tree-node.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../model/enums/manual-test-status.enum";
import {ManualTestModel} from "../../model/manual-test.model";
import {ManualTestStepExeModel} from "./manual-step-exe.model";

export class ManualTestExeModel extends ManualTestModel implements Serializable<ManualTestExeModel>, TreeNodeModel {

    id:string = IdUtils.getTemporaryId();
    path:Path;
    text:string;
    description:string;
    tags:Array<string> = [];
    steps:Array<ManualTestStepExeModel> = [];

    testStatus: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;
    comments: string;

    static createInstanceFrom(manualTestModel: ManualTestModel): ManualTestExeModel {
        let instance = new ManualTestExeModel();
        instance.id = manualTestModel.id;
        instance.path = manualTestModel.path;
        instance.text = manualTestModel.text;
        instance.description = manualTestModel.description;
        instance.tags = manualTestModel.tags;

        for (const step of manualTestModel.steps) {
            instance.steps.push(
                ManualTestStepExeModel.createInstanceFrom(step)
            )
        }
        return instance;
    }

    deserialize(input: Object): ManualTestExeModel {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.text = input['text'];
        this.description = input['description'];

        for (let tag of (input['tags']) || []) {
            this.tags.push(tag);
        }

        for (let step of (input['steps']) || []) {
            this.steps.push(new ManualTestStepExeModel().deserialize(step));
        }

        if (input["testStatus"]) {
            this.testStatus = ManualTestStatus.fromString(input["testStatus"]);
        }

        this.comments = input['comments'];


        return this;
    }

    serialize() {
        let response = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"text":' + JsonUtil.stringify(this.text) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.serializeArray(this.tags) + ',' +
            '"steps":' + JsonUtil.serializeArrayOfSerializable(this.steps) + ',' +
            '"comments":' + JsonUtil.stringify(this.comments);

        if (this.testStatus) {
            response += ',"testStatus":' + JsonUtil.stringify(this.testStatus.toString())
        }
        response += '}';
        return response;
    }
}
