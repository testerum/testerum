import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ObjectUtil} from "../../utils/object.util";
import {StringUtils} from "../../utils/string-utils.util";

@Directive({
    selector: '[isNotBlankValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: IsNotBlankValidatorDirective, multi: true}]
})
export class IsNotBlankValidatorDirective implements Validator {

    validate(control: AbstractControl): {[key: string]: any} {
        let value = control.value;
        if(value == null) return this.isNotBlankResponse(control);

        if(StringUtils.isString(value)) {
            let isBlank = value.trim().length === 0;
            if(isBlank) {
                return this.isNotBlankResponse(control);
            }
        }

        return null;
    }

    isNotBlankResponse(control: AbstractControl) {
        return {'required': {value: control.value}}
    }
}
