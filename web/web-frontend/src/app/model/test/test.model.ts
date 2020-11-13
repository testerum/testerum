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
import {ArrayUtil} from "../../utils/array.util";
import {StepDefUtil} from "../step/util/step-def.util";

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
    afterHooks: Array<StepCall> = [];

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

        this.afterHooks = [];
        for (let afterHookJson of (input['afterHooks'] || [])) {
            let afterHook = new StepCall().deserialize(afterHookJson);
            if (afterHook.stepDef instanceof UndefinedStepDef) {
                afterHook.stepDef.path = this.path.getParentPath();
            }
            this.afterHooks.push(afterHook);
        }
        let stepCallsAndStepHooks = this.stepCalls.concat(this.afterHooks);
        StepDefUtil.fixStepDefInstance(stepCallsAndStepHooks);

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
            '"afterHooks":' + JsonUtil.serializeArrayOfSerializable(this.afterHooks) + ',' +
            '"warnings": []' +
            '}'
    }

    clone(): TestModel {
        let objectAsJson = JSON.parse(this.serialize());
        return new TestModel().deserialize(objectAsJson);
    }
}
