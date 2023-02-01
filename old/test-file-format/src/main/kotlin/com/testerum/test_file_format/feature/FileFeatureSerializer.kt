package com.testerum.test_file_format.feature

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileAfterAllHookSerializer
import com.testerum.test_file_format.common.step_call.FileAfterEachHookSerializer
import com.testerum.test_file_format.common.step_call.FileBeforeAllHookSerializer
import com.testerum.test_file_format.common.step_call.FileBeforeEachHookSerializer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import java.io.Writer

object FileFeatureSerializer : BaseSerializer<FileFeature>() {

    override fun serialize(source: FileFeature, destination: Writer, indentLevel: Int) {
        serializeDescription(source.description, destination, indentLevel)
        serializeTags(source.tags, destination, indentLevel)
        serializeBeforeAllHooks(source.beforeAllHooks, destination, indentLevel)
        serializeBeforeEachHooks(source.beforeEachHooks, destination, indentLevel)
        serializeAfterEachHooks(source.afterEachHooks, destination, indentLevel)
        serializeAfterAllHooks(source.afterAllHooks, destination, indentLevel)
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

    private fun serializeBeforeAllHooks(hooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (beforeHook in hooks) {
            FileBeforeAllHookSerializer.serialize(beforeHook, destination, indentLevel)
        }
    }

    private fun serializeBeforeEachHooks(hooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (beforeHook in hooks) {
            FileBeforeEachHookSerializer.serialize(beforeHook, destination, indentLevel)
        }
    }

    private fun serializeAfterEachHooks(hooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (beforeHook in hooks) {
            FileAfterEachHookSerializer.serialize(beforeHook, destination, indentLevel)
        }
    }

    private fun serializeAfterAllHooks(hooks: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.write("\n")

        for (beforeHook in hooks) {
            FileAfterAllHookSerializer.serialize(beforeHook, destination, indentLevel)
        }
    }
}
