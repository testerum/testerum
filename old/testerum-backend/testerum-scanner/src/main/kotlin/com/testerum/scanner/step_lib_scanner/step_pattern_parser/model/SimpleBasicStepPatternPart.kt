package com.testerum.scanner.step_lib_scanner.step_pattern_parser.model

sealed class SimpleBasicStepPatternPart

data class TextSimpleBasicStepPatternPart(val text: String) : SimpleBasicStepPatternPart()

data class ParamSimpleBasicStepPatternPart(val name: String) : SimpleBasicStepPatternPart()
