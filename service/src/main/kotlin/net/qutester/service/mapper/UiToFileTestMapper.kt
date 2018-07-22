package net.qutester.service.mapper

import com.testerum.test_file_format.testdef.FileTestDef
import net.qutester.model.test.TestModel

open class UiToFileTestMapper(private val stepDefMapper: UiToFileStepDefMapper) {

    fun mapToFileModel(uiTest: TestModel): FileTestDef {
        val fileDescription: String? = uiTest.description

        return FileTestDef(
                name = uiTest.text,
                isManual =  uiTest.isManual,
                description = fileDescription,
                tags = emptyList(),
                steps = stepDefMapper.mapStepCalls(uiTest.stepCalls)
        )
    }

}
