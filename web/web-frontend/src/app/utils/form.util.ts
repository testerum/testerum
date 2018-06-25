import {NgForm} from "@angular/forms";
import {FormValidationModel} from "../model/exception/form-validation.model";

export class FormUtil {

    static setErrorsToForm(form: NgForm, formValidationModel:FormValidationModel): void {
        let control = form.control;

        for (let filedKey in formValidationModel.fieldsWithValidationErrors) {
            let validationError = formValidationModel.fieldsWithValidationErrors[filedKey];

            let obj = {};
            obj[validationError] = true;
            control.get(filedKey).setErrors(
                obj
            );
        }
    }

    static addErrorToForm(form: NgForm, formFieldName: string, errorCode: string) {
        let control = form.control;

        let obj = {};
        obj[errorCode] = true;
        control.get(formFieldName).setErrors(
            obj
        );
    }
}
