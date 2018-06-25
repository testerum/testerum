import {AbstractControl, NG_VALIDATORS, Validator} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {StepPatternPart} from "../../../../../model/text/parts/step-pattern-part.model";
import {ParamPatternParser} from "../../../../../model/text/parser/param-pattern-parser";
import {TextStepPatternPart} from "../../../../../model/text/parts/text-step-pattern-part.model";
import {ObjectUtil} from "../../../../../utils/object.util";
import {Arg} from "../../../../../model/arg/arg.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../../../../functionalities/resources/editors/resource-map.enum";


@Directive({
    selector: '[argValueValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: ArgValueValidatorDirective, multi: true}]
})
export class ArgValueValidatorDirective implements Validator {
    @Input() arg:Arg;
    @Input() stepParameter: ParamStepPatternPart;


    validate(control: AbstractControl): {[key: string]: any} {
        let inputValue: string = control.value;

        let nonVariableReferenceInput = "";
        let stepPatternParts: Array<StepPatternPart> = ParamPatternParser.parsePatternText(inputValue);
        for (const stepPatternPart of stepPatternParts) {
            if (stepPatternPart instanceof TextStepPatternPart) {
                nonVariableReferenceInput += (stepPatternPart as TextStepPatternPart).text;
            }
        }

        if(!nonVariableReferenceInput) {
            return null;
        }

        if (this.stepParameter.uiType == ResourceMapEnum.NUMBER.serverType && !ObjectUtil.isANumber(nonVariableReferenceInput)) {
            return this.returnError("argValueValidator_notANumber", control)
        }


        return null;
    }

    returnError(errorKey: string, control: AbstractControl) {
        return {errorKey: {value: control.value}}
    }
}
