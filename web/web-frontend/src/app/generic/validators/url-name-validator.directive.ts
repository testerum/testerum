import {AbstractControl, NG_VALIDATORS, Validator, ValidatorFn} from "@angular/forms";
import {Directive, Input} from "@angular/core";
import {ObjectUtil} from "../../utils/object.util";

@Directive({
    selector: '[urlNameValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: UrlNameValidatorDirective, multi: true}]
})
export class UrlNameValidatorDirective implements Validator {

    regexp = new RegExp('^[-0-9a-zA-Z$_.,\'()]+$');

    validate(control: AbstractControl): {[key: string]: any} {
        let inputValue = control.value;

        if(!inputValue) {
            return null;
        }

        if (!this.regexp.test(inputValue)) {
            return this.notValidUrlNameResponse(control)
        }

        return null;
    }

    notValidUrlNameResponse(control: AbstractControl) {
        return {'invalidUrlName': {value: control.value}}
    }
}
