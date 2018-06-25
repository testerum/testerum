import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ParamPatternParser} from "../../model/text/parser/param-pattern-parser";
import {StepPatternPart} from "../../model/text/parts/step-pattern-part.model";
import {TextStepPatternPart} from "../../model/text/parts/text-step-pattern-part.model";
import {StringUtils} from "../../utils/string-utils.util";
import {ObjectUtil} from "../../utils/object.util";

@Directive({
    selector: '[portValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: PortValidatorDirective, multi: true}]
})
export class PortValidatorDirective implements Validator {
    @Input() forbiddenName: string;

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

        if (!ObjectUtil.isANumber(nonVariableReferenceInput)) {
            return this.notValidPortResponse(control)
        }

        if (+nonVariableReferenceInput < 0 || +nonVariableReferenceInput > 65535) {
            return this.notValidPortResponse(control)
        }

        return null;
    }

    notValidPortResponse(control: AbstractControl) {
        return {'portValidator': {value: control.value}}
    }
}
