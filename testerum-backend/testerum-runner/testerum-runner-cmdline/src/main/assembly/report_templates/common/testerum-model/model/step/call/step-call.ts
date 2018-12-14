import {StepDef, StepDefType} from "../def/step-def";
import {Arg} from "./arg";
import {MarshallingUtils} from "../../../json-marshalling/marshalling-utils";
import {BasicStepDef} from "../def/basic-step-def";
import {ComposedStepDef} from "../def/composed-step-def";
import {UndefinedStepDef} from "../def/undefined-step-def";

export class StepCall {

    constructor(public readonly id: string,
                public readonly stepDef: StepDef,
                public readonly args: Array<Arg>) {}

    static parse(input: Object): StepCall {
        const id = input["id"];
        const stepDef: StepDef = MarshallingUtils.parsePolymorphically<StepDef>(input["stepDef"], {
            [StepDefType[StepDefType.BASIC_STEP]]: BasicStepDef,
            [StepDefType[StepDefType.COMPOSED_STEP]]: ComposedStepDef,
            [StepDefType[StepDefType.UNDEFINED_STEP]]: UndefinedStepDef
        });
        const args = MarshallingUtils.parseList(input["args"], Arg);

        return new StepCall(id, stepDef, args);
    }

}
