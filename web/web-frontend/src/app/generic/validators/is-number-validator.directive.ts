import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ObjectUtil} from "../../utils/object.util";

@Directive({
    selector: '[isNumberValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: IsNumberValidatorDirective, multi: true}]
})
export class IsNumberValidatorDirective implements Validator {

    validate(control: AbstractControl): {[key: string]: any} {
        let inputValue = control.value;

        if(!inputValue) {
            return null;
        }

        if (!ObjectUtil.isANumber(inputValue)) {
            return this.notValidNumberResponse(control)
        }

        return null;
    }

    notValidNumberResponse(control: AbstractControl) {
        return {'isNumberValidator': {value: control.value}}
    }
}
