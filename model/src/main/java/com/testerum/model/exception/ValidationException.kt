package com.testerum.model.exception

import com.testerum.model.exception.model.ValidationModel

class ValidationException : RuntimeException {

    val validationModel: ValidationModel

    override val message: String?
        get() = validationModel.toString()

    constructor() {
        this.validationModel = ValidationModel.EMPTY
    }

    constructor(validationModel: ValidationModel) {
        this.validationModel = validationModel
    }

    fun addFiledValidationError(filedName:String, errorCode:String): ValidationException {
        validationModel.fieldsWithValidationErrors[filedName] = errorCode
        return this
    }

}
