import {MyError} from "./my-error.model";
import {ErrorCode} from "./enums/error-code.enum";
import {ValidationModel} from "./validation.model";
import {Serializable} from "../infrastructure/serializable.model";

export class ValidationErrorResponse extends MyError implements Serializable<ValidationErrorResponse> {

    errorCode: ErrorCode;
    validationModel: ValidationModel;

    deserialize(input: Object): ValidationErrorResponse {
        this.errorCode = ErrorCode.fromString(input['errorCode']);
        this.validationModel = new ValidationModel().deserialize(input["validationModel"]);

        return this;
    }

    serialize(): string {
        return null;
    }

}
