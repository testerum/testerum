import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ObjectUtil} from "../../utils/object.util";

@Directive({
    selector: '[isNotBlankValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: IsNotBlankValidatorDirective, multi: true}]
})
export class IsNotBlankValidatorDirective implements Validator {

    validate(control: AbstractControl): {[key: string]: any} {
        let isBlank = (control.value || '').trim().length === 0;
        if(isBlank) {
            return this.isNotBlankResponse(control);
        }

        return null;
    }

    isNotBlankResponse(control: AbstractControl) {
        return {'required': {value: control.value}}
    }
}
