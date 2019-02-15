import { Injectable } from '@angular/core';
import {TestModel} from "../../model/test/test.model";
import {StepDef} from "../../model/step-def.model";
import {Path} from "../../model/infrastructure/path/path.model";
import {UndefinedStepDef} from "../../model/undefined-step-def.model";
import {PathUtil} from "../../utils/path.util";
import {ComposedStepDef} from "../../model/composed-step-def.model";

@Injectable()
export class ModelRepairerService {

    repairTestModel(test: TestModel): TestModel {

        for (const stepCall of test.stepCalls) {
            stepCall.stepDef = this.repairStepDef(stepCall.stepDef, test.path);
        }

        return test;
    }

    repairStepDef(stepDef: StepDef, contextPath: Path): StepDef {
        if (stepDef instanceof UndefinedStepDef) {
            if (stepDef.path == null || stepDef.path.isEmpty()) {
                stepDef.path = PathUtil.generateStepDefPath(contextPath, stepDef.phase, stepDef.stepPattern.getPatternText());
            }
        }

        if (stepDef instanceof ComposedStepDef) {
            let stepCalls = stepDef.stepCalls;
            for (const stepCall of stepCalls) {
                stepCall.stepDef = this.repairStepDef(stepCall.stepDef, contextPath);
            }
        }

        return stepDef;
    }
}
