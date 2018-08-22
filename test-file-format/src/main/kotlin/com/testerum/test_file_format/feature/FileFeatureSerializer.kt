package com.testerum.test_file_format.feature

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileAfterHookSerializer
import com.testerum.test_file_format.common.step_call.FileBeforeHookSerializer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import java.io.Writer

object FileFeatureSerializer : BaseSerializer<FileFeature>() {

    override fun serialize(source: FileFeature, destination: Writer, indentLevel: Int) {
        serializeDescription(source.description, destination, indentLevel)
        serializeTags(source.tags, destination, indentLevel)
        serializeBeforeHooks(source.beforeHooks, destination, indentLevel)
        serializeAfterHooks(source.afterHooks, destination, indentLevel)
    }

    private fun serializeDescription(description: String?, destination: Writer, indentLevel: Int) {
        if (description == null || description.isEmpty()) {
            return
        }

        FileDescriptionSerializer.serialize(description, destination, indentLevel)
    }

    private fun serializeTags(tags: List<String>, destination: Writer, indentLevel: Int) {
        if (!tags.isNotEmpty()) {
            return
        }

        destination.write("\n")
        FileTagsSerializer.serialize(tags, destination, indentLevel)
    }

    private fun serializeBeforeHooks(beforeHooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (beforeHooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (beforeHook in beforeHooks) {
            FileBeforeHookSerializer.serialize(beforeHook, destination, indentLevel)
        }
    }

    private fun serializeAfterHooks(afterHooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (afterHooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (afterHook in afterHooks) {
            FileAfterHookSerializer.serialize(afterHook, destination, indentLevel)
        }
    }

}
