package com.testerum.test_file_format.common.step_call.part.arg_part

sealed class FileArgPart

data class FileTextArgPart(val text: String): FileArgPart()
data class FileExpressionArgPart(val text: String): FileArgPart()

