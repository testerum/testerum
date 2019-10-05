import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ObjectUtil} from "../../utils/object.util";

@Directive({
    selector: '[paramNameValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: ParamNameValidatorDirective, multi: true}]
})
export class ParamNameValidatorDirective implements Validator {

    static regexp = new RegExp('^[a-zA-Z_][0-9a-zA-Z_]*$');

    validate(control: AbstractControl): {[key: string]: any} {
        let inputValue = control.value;

        if(!inputValue) {
            return null;
        }

        if (!ParamNameValidatorDirective.isValidParamName(inputValue)) {
            return this.notValidParamNameResponse(control)
        }

        return null;
    }

    notValidParamNameResponse(control: AbstractControl) {
        return {'invalidParamName': {value: control.value}}
    }

    static isValidParamName(paramName: string): boolean {
        return ParamNameValidatorDirective.regexp.test(paramName)
    }
}
