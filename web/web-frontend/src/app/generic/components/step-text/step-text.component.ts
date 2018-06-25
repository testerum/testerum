import {Component, OnInit, Input} from '@angular/core';
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {StepTextPartEnum} from "../../../model/enums/step-text-part.enum";
import {StepDef} from "../../../model/step-def.model";
import {StepPatternPart} from "../../../model/text/parts/step-pattern-part.model";
import {ParamStepPatternPart} from "../../../model/text/parts/param-step-pattern-part.model";
import {StepCall} from "../../../model/step-call.model";
import {UndefinedStepDef} from "../../../model/undefined-step-def.model";
import {StringUtils} from "../../../utils/string-utils.util";

@Component({
    moduleId: module.id,
    selector: 'step-text',
    templateUrl: 'step-text.component.html',
    styleUrls: ['step-text.component.css']
})
export class StepTextComponent<T extends StepDef> implements OnInit {

    @Input() stepDef: T;
    @Input() stepCall:StepCall;
    @Input() showPhase:boolean = true;

    @Input() showAsListItem = false;
    @Input() showPhaseAsAnd = false;

    StepPhaseEnum = StepPhaseEnum;
    StepTextPartEnum = StepTextPartEnum;

    ngOnInit() {
    }

    getStepPhaseAsString(): string {
        if(this.showPhaseAsAnd) {
            return StringUtils.toTitleCase(this.StepPhaseEnum[this.StepPhaseEnum.AND]);
        }
        return StringUtils.toTitleCase(this.StepPhaseEnum[this.stepDef.phase]);
    }

    isParamPatternPart(patternPart:StepPatternPart):boolean {
        return patternPart instanceof ParamStepPatternPart;
    }

    getParamIndexFromTextPartIndex(textPartIndex:number):number {
        let paramIndex = -1;

        for (let i = 0; i <= textPartIndex; i++) {
            let patternPart = this.stepDef.stepPattern.patternParts[i];
            if(patternPart instanceof ParamStepPatternPart) {
                paramIndex++
            }
        }
        return paramIndex;
    }

    isUndefinedStepDef(): boolean {
        return this.stepDef instanceof UndefinedStepDef;
    }
}
