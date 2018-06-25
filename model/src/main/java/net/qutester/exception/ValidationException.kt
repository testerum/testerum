package net.qutester.exception

import net.qutester.exception.model.ValidationModel

class ValidationException : RuntimeException {

    val validationModel: ValidationModel

    constructor() {
        this.validationModel = ValidationModel()
    }

    constructor(validationModel: ValidationModel ) {
        this.validationModel = validationModel
    }

    fun addFiledValidationError(filedName:String, errorCode:String): ValidationException {
        validationModel.fieldsWithValidationErrors.put(filedName, errorCode)
        return this;
    }
}