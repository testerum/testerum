import {NgForm} from "@angular/forms";
import {ValidationModel} from "../model/exception/validation.model";
import {HttpErrorResponse} from "@angular/common/http";
import {MyError} from "../model/exception/my-error.model";
import {ErrorCode} from "../model/exception/enums/error-code.enum";
import {ValidationErrorResponse} from "../model/exception/validation-error-response.model";

export class FormUtil {

    static setErrorsToForm(form: NgForm, error: any): void {

        let formValidationModel: ValidationModel;
        if(error instanceof ValidationErrorResponse) {
            formValidationModel = error.validationModel;
        }

        if (error instanceof ValidationModel) {
            formValidationModel = error;
        }

        if(error instanceof HttpErrorResponse) {
            let errorResponse: MyError = error.error;

            if (errorResponse.errorCode.toString() == ErrorCode.VALIDATION.enumAsString) {
                let validationException: ValidationErrorResponse = new ValidationErrorResponse().deserialize(errorResponse);
                formValidationModel = validationException.validationModel;

                if (formValidationModel.globalMessage) {
                    throw error; //to be handled as a Generic Validation Exception by the error handler;
                }
            } else {
                throw error;
            }
        }

        if (formValidationModel == null) {
            return
        }

        let control = form.control;

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
