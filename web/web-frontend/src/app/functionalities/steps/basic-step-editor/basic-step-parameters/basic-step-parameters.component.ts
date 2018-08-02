import {Component, Input} from '@angular/core';
import {StepPatternPart} from "../../../../model/text/parts/step-pattern-part.model";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../../resources/editors/resource-map.enum";

@Component({
    moduleId: module.id,
    selector: 'basic-step-parameters',
    templateUrl: 'basic-step-parameters.component.html',
    styleUrls: ['basic-step-parameters.component.scss']
})
export class BasicStepParametersComponent {

    @Input() patternParts: Array<StepPatternPart>;

    patternHasParameters(): boolean {
        for (let part of this.patternParts) {
            if (part instanceof ParamStepPatternPart) {
                return true;
            }
        }
        return false;
    }

    isParamPattern(stepPatternPart: StepPatternPart): boolean {
        if (stepPatternPart instanceof ParamStepPatternPart) {
            return true;
        }
        return false;
    }

    isEnumType(type: any):boolean {
        return type == ResourceMapEnum.ENUM;
    }
}
