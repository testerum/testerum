package com.testerum.test_file_format.common.step_call.part


sealed class FileStepCallPart

data class FileTextStepCallPart(val text: String): FileStepCallPart()

data class FileArgStepCallPart(val text: String): FileStepCallPart()

