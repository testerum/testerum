import {Serializable} from "../../infrastructure/serializable.model";
import {StepCall} from "../../step/step-call.model";
import {Path} from "../../infrastructure/path/path.model";
import {UndefinedStepDef} from "../../step/undefined-step-def.model";
import {StepDefUtil} from "../../step/util/step-def.util";
import {JsonUtil} from "../../../utils/json.util";

export class Hooks implements Serializable<Hooks> {
    beforeAll: Array<StepCall> = [];
    beforeEach: Array<StepCall> = [];
    afterEach: Array<StepCall> = [];
    afterAll: Array<StepCall> = [];

    parentEntityPath: Path;

    constructor(parentEntityPath: Path) {
        this.parentEntityPath = parentEntityPath;
    }

    deserialize(input: Object): Hooks {
        this.beforeAll = this.deserializeStepCallArray(input['beforeAll'])
        this.beforeEach = this.deserializeStepCallArray(input['beforeEach'])
        this.afterEach = this.deserializeStepCallArray(input['afterEach'])
        this.afterAll = this.deserializeStepCallArray(input['afterAll'])

        let allFeatureStepHooks = this.beforeAll.concat(this.beforeEach).concat(this.afterEach).concat(this.afterAll);
        StepDefUtil.fixStepDefInstance(allFeatureStepHooks);

        return this;
    }

    private deserializeStepCallArray(inputElement: Array<Object>): Array<StepCall> {
        let hooks = [];
        for (let hookJson of (inputElement || [])) {
            let hook = new StepCall().deserialize(hookJson);
            if (hook.stepDef instanceof UndefinedStepDef) {
                hook.stepDef.path = this.parentEntityPath.getParentPath();
            }
            hooks.push(hook);
        }
        return hooks;
    }

    serialize(): string {
        return '' +
            '{' +
            ' "beforeAll":' + JsonUtil.serializeArrayOfSerializable(this.beforeAll) +
            ',"beforeEach":' + JsonUtil.serializeArrayOfSerializable(this.beforeEach) +
            ',"afterEach":' + JsonUtil.serializeArrayOfSerializable(this.afterEach) +
            ',"afterAll":' + JsonUtil.serializeArrayOfSerializable(this.afterAll) +
            '}'
    }
}
