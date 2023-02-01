
export class ValidationResultModel {
    isValid: boolean;
    message: string;

    constructor(isValid: boolean, message: string = null) {
        this.isValid = isValid;
        this.message = message;
    }
}
