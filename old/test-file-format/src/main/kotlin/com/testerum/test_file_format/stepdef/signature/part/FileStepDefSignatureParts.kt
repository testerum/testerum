package com.testerum.test_file_format.stepdef.signature.part

sealed class FileStepDefSignaturePart

data class FileTextStepDefSignaturePart(val text: String) : FileStepDefSignaturePart()
data class FileParamStepDefSignaturePart(val name: String, val type: String) : FileStepDefSignaturePart()
