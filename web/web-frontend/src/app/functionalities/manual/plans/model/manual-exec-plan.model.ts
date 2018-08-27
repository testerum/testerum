import {ManualExecPlanStatus} from "./enums/manual-exec-plan-status.enum";
import {Serializable} from "../../../../model/infrastructure/serializable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {TestModel} from "../../../../model/test/test.model";
import {JsonUtil} from "../../../../utils/json.util";

export class ManualExecPlan implements Serializable<ManualExecPlan>{

    path: Path;
    oldPath: Path;
    environment: string;
    applicationVersion: string;
    status: ManualExecPlanStatus = ManualExecPlanStatus.IN_EXECUTION;
    createdDate: Date;
    finalizedDate: Date;

    testsToExecute: Array<TestModel> = [];

    totalTests: number = 0;

    passedTests: number = 0;
    failedTests: number = 0;
    blockedTests: number = 0;
    notApplicableTests: number = 0;
    notExecutedTests: number = 0;


    deserialize(input: Object): ManualExecPlan {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.environment = input['environment'];
        this.applicationVersion = input['applicationVersion'];
        this.status = ManualExecPlanStatus.fromString(input['status']);

        if (input['createdDate']) {
            this.createdDate = new Date(input['createdDate']);
        }
        if (input['finalizedDate']) {
            this.finalizedDate = new Date(input['finalizedDate']);
        }
        for (let test of (input['testsToExecute']) || []) {
            this.testsToExecute.push(new TestModel().deserialize(test));
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
            ',"environment":' + JsonUtil.stringify(this.environment) +
            ',"applicationVersion":' + JsonUtil.stringify(this.applicationVersion) +
            ',"status":' + JsonUtil.stringify(this.status.toString());

        if (this.createdDate) {
            response += ',"createdDate":' + JsonUtil.stringify(this.createdDate.toJSON());
        }
        if (this.finalizedDate) {
            response += ',"finalizedDate":' + JsonUtil.stringify(this.finalizedDate.toJSON());
        }
        if (this.testsToExecute) {
            response += ',"testsToExecute":' + JsonUtil.serializeArrayOfSerializable(this.testsToExecute);
        }
        response += '}';

        return response;
    }
}
