import {NgForm} from "@angular/forms";
import {FormValidationModel} from "../model/exception/form-validation.model";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorResponse} from "../model/exception/error-response.model";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";

export class FormUtil {

    static setErrorsToForm(form: NgForm, error: any): void {

        let validationException: ValidationErrorResponse;
        if(error instanceof ValidationErrorResponse) {
            validationException = error;
        }

        if(error instanceof HttpErrorResponse) {
            let errorResponse: ErrorResponse = error.error;

            if (errorResponse.errorCode.toString() == ErrorCode.VALIDATION.enumAsString) {
                validationException = new ValidationErrorResponse().deserialize(errorResponse);
            }
        }

        if (validationException == null) {
            return
        }

        let control = form.control;
        let formValidationModel: FormValidationModel = validationException.validationModel;

        formValidationModel.fieldsWithValidationErrors.forEach((validationError:string, filedKey:string) => {
            let obj = {};
            obj[validationError] = true;
            control.get(filedKey).setErrors(
                obj
            );
        });
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
