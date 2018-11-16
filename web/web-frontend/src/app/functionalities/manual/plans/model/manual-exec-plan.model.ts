import {ManualExecPlanStatus} from "./enums/manual-exec-plan-status.enum";
import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ManualTreeTest} from "./manual-tree-test.model";

export class ManualExecPlan implements Serializable<ManualExecPlan>{

    path: Path;
    oldPath: Path;
    name: string;
    description: string;
    status: ManualExecPlanStatus = ManualExecPlanStatus.IN_EXECUTION;
    createdDate: Date;
    finalizedDate: Date;

    manualTreeTests: ManualTreeTest[] = [];

    totalTests: number = 0;

    passedTests: number = 0;
    failedTests: number = 0;
    blockedTests: number = 0;
    notApplicableTests: number = 0;
    notExecutedTests: number = 0;


    deserialize(input: Object): ManualExecPlan {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.name = input['name'];
        this.description = input['description'];
        this.status = ManualExecPlanStatus.fromString(input['status']);

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
        this.notExecutedTests = input['notExecutedTests'];

        return this;
    }

    serialize(): string {
        let response = "" +
            '{' +
            '"path":' + JsonUtil.serializeSerializable(this.path) +
            ',"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) +
            ',"name":' + JsonUtil.stringify(this.name) +
            ',"description":' + JsonUtil.stringify(this.description) +
            ',"status":' + JsonUtil.stringify(this.status.toString());

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
