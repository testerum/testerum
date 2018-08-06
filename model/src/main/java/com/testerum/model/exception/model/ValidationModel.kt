package com.testerum.model.exception.model

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationModel {

    constructor()

    @JsonProperty("globalValidationMessage") var globalValidationMessage: String? = null
    @JsonProperty("globalValidationMessageDetails") var globalValidationMessageDetails: String? = null
    @JsonProperty("fieldsWithValidationErrors") val fieldsWithValidationErrors: MutableMap<String, String> = mutableMapOf()

    constructor(globalValidationMessage: String?, globalValidationMessageDetails: String? = null) {
        this.globalValidationMessage = globalValidationMessage
        this.globalValidationMessageDetails = globalValidationMessageDetails
    }
}


