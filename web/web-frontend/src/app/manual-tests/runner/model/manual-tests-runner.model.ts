import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonUtil} from "../../../utils/json.util";
import {ManualTestsRunnerStatus} from "./enums/manual-tests-runner-status.enum";
import {ManualTestExeModel} from "./manual-test-exe.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";

export class ManualTestsRunner implements Serializable<ManualTestsRunner>{

    path: Path;
    oldPath: Path;
    environment: string;
    applicationVersion: string;
    status: ManualTestsRunnerStatus = ManualTestsRunnerStatus.IN_EXECUTION;
    createdDate: Date;
    finalizedDate: Date;

    testsToExecute: Array<ManualTestExeModel> = [];

    totalTests: number = 0;

    passedTests: number = 0;
    failedTests: number = 0;
    blockedTests: number = 0;
    notApplicableTests: number = 0;
    notExecutedTests: number = 0;


    deserialize(input: Object): ManualTestsRunner {
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.environment = input['environment'];
        this.applicationVersion = input['applicationVersion'];
        this.status = ManualTestsRunnerStatus.fromString(input['status']);

        if (input['createdDate']) {
            this.createdDate = new Date(input['createdDate']);
        }
        if (input['finalizedDate']) {
            this.finalizedDate = new Date(input['finalizedDate']);
        }
        for (let test of (input['testsToExecute']) || []) {
            this.testsToExecute.push(new ManualTestExeModel().deserialize(test));
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
