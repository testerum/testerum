import {ErrorResponse} from "./error-response.model";
import {ErrorCode} from "./enums/error-code.enum";
import {FormValidationModel} from "./form-validation.model";

export class ValidationErrorResponse implements ErrorResponse, Serializable<ValidationErrorResponse> {

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
