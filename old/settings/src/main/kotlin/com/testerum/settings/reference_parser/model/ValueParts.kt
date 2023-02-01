package com.testerum.settings.reference_parser.model

sealed class ValuePart

data class FixedValuePart(val text: String): ValuePart()
data class ReferenceValuePart(val key: String): ValuePart()
