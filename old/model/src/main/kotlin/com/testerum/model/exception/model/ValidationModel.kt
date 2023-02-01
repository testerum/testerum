package com.testerum.model.exception.model

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationModel {

    companion object {
        val EMPTY = ValidationModel()
    }

    constructor()

    @JsonProperty("globalMessage") var globalMessage: String? = null
    @JsonProperty("globalHtmlMessage") var globalHtmlMessage: String? = null
    @JsonProperty("globalMessageDetails") var globalMessageDetails: String? = null
    @JsonProperty("fieldsWithValidationErrors") val fieldsWithValidationErrors: MutableMap<String, String> = mutableMapOf()

    constructor(globalMessage: String?, globalHtmlMessage: String? = null, globalMessageDetails: String? = null) {
        this.globalMessage = globalMessage
        this.globalHtmlMessage = globalHtmlMessage
        this.globalMessageDetails = globalMessageDetails
    }

    override fun toString(): String {
        return "ValidationModel(globalMessage=$globalMessage, globalMessageDetails=$globalMessageDetails, fieldsWithValidationErrors=$fieldsWithValidationErrors)"
    }
}


