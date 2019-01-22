import {MyError} from "./my-error.model";
import {ErrorCode} from "./enums/error-code.enum";
import {FormValidationModel} from "./form-validation.model";
import {Serializable} from "../infrastructure/serializable.model";

export class ValidationErrorResponse implements MyError, Serializable<ValidationErrorResponse> {

    errorCode: ErrorCode;
    validationModel: FormValidationModel;

    deserialize(input: Object): ValidationErrorResponse {
        this.errorCode = ErrorCode.fromString(input['errorCode']);
        this.validationModel = new FormValidationModel().deserialize(input["validationModel"]);

        return this;
    }

    serialize(): string {
        return null;
    }

}
