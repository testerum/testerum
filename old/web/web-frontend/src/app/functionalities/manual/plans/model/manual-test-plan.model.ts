import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTreeTest} from "./manual-tree-test.model";

export class ManualTestPlan implements Serializable<ManualTestPlan>{

    path: Path;
    oldPath: Path;
    name: string;
    description: string;
    isFinalized: boolean;
    createdDate: Date;
    finalizedDate: Date;

    manualTreeTests: ManualTreeTest[] = [];

    totalTests: number = 0;

    passedTests: number = 0;
    failedTests: number = 0;
    blockedTests: number = 0;
    notApplicableTests: number = 0;
    notExecutedOrInProgressTests: number = 0;


    deserialize(input: Object): ManualTestPlan {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.name = input['name'];
        this.description = input['description'];
        this.isFinalized = input['isFinalized'];

        if (input['createdDate']) {
            this.createdDate = new Date(input['createdDate']);
        }
        if (input['finalizedDate']) {
            this.finalizedDate = new Date(input['finalizedDate']);
        }
        for (let manualTreeTest of (input['manualTreeTests']) || []) {
            this.manualTreeTests.push(new ManualTreeTest().deserialize(manualTreeTest));
        }

        this.totalTests = input['totalTests'];
        this.passedTests = input['passedTests'];
        this.failedTests = input['failedTests'];
        this.blockedTests = input['blockedTests'];
        this.notApplicableTests = input['notApplicableTests'];
        this.notExecutedOrInProgressTests = input['notExecutedOrInProgressTests'];

        return this;
    }

    serialize(): string {
        let response = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) +
            ',"name":' + JsonUtil.stringify(this.name) +
            ',"description":' + JsonUtil.stringify(this.description) +
            ',"finalized":' + JsonUtil.stringify(this.isFinalized);

        if (this.createdDate) {
            response += ',"createdDate":' + JsonUtil.stringify(this.createdDate.toJSON());
        }
        if (this.finalizedDate) {
            response += ',"finalizedDate":' + JsonUtil.stringify(this.finalizedDate.toJSON());
        }
        if (this.manualTreeTests) {
            response += ',"manualTreeTests":' + JsonUtil.serializeArrayOfSerializable(this.manualTreeTests);
        }
        response += '}';

        return response;
    }
}
