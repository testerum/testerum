import {TreeNodeModel} from "../infrastructure/tree-node.model";
import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {StepCall} from "../step/step-call.model";
import {Path} from "../infrastructure/path/path.model";
import {TestProperties} from "./test-properties.model";
import {Warning} from "../warning/Warning";
import {Serializable} from "../infrastructure/serializable.model";
import {ComposedStepDef} from "../step/composed-step-def.model";
import {UndefinedStepDef} from "../step/undefined-step-def.model";
import {Scenario} from "./scenario/scenario.model";

export class TestModel implements Serializable<TestModel>, TreeNodeModel {

    id:string = IdUtils.getTemporaryId();
    path:Path;
    oldPath:Path;
    properties: TestProperties  = new TestProperties();
    name:string;
    description:string;
    tags: Array<string> = [];

    scenarios: Array<Scenario> = [];
    stepCalls: Array<StepCall> = [];

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    deserialize(input: Object): TestModel {
        this.id = input['id'];
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.properties = new TestProperties().deserialize(input['properties']);
        this.name = input['name'];
        this.description = input['description'];
        this.tags = input['tags'] || [];

        this.scenarios = [];
        for (let scenarioJson of (input['scenarios'] || [])) {
            this.scenarios.push(
                new Scenario().deserialize(scenarioJson)
            );
        }

        this.stepCalls = [];
        for (let stepCallJson of (input['stepCalls'] || [])) {
            let stepCall = new StepCall().deserialize(stepCallJson);
            if (stepCall.stepDef instanceof UndefinedStepDef) {
                stepCall.stepDef.path = this.path.getParentPath();
            }
            this.stepCalls.push(stepCall);
        }
        this.fixStepDefInstance(this.stepCalls);

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
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"properties":' + JsonUtil.serializeSerializable(this.properties) + ',' +
            '"name":' + JsonUtil.stringify(this.name) + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.stringify(this.tags) + ',' +
            '"scenarios":' + JsonUtil.serializeArrayOfSerializable(this.scenarios) + ',' +
            '"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) + ',' +
            '"warnings": []' +
            '}'
    }

    clone(): TestModel {
        let objectAsJson = JSON.parse(this.serialize());
        return new TestModel().deserialize(objectAsJson);
    }

    private fixStepDefInstance(stepCalls: Array<StepCall>) {
        let uniqueStepDefs = new Map();
        for (const stepCall of stepCalls) {
            if (!(stepCall.stepDef instanceof ComposedStepDef)) {
                continue
            }

            let stepDefKey = stepCall.stepDef.path.toString();
            let sharedStepDefInstance = uniqueStepDefs.get(stepDefKey);

            if (sharedStepDefInstance) {
                stepCall.stepDef = sharedStepDefInstance;
            } else {
                uniqueStepDefs.set(stepDefKey, stepCall.stepDef)
            }
        }
    }
}
