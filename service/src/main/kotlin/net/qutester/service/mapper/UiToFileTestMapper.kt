package net.qutester.service.mapper

import com.testerum.model.test.TestModel
import com.testerum.model.test.TestProperties
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties

open class UiToFileTestMapper(private val stepDefMapper: UiToFileStepDefMapper) {

    fun mapToFileModel(uiTest: TestModel): FileTestDef {
        val fileDescription: String? = uiTest.description

        return FileTestDef(
                name = uiTest.text,
                properties = mapTestProperties(uiTest.properties),
                description = fileDescription,
                tags = uiTest.tags,
                steps = stepDefMapper.mapStepCalls(uiTest.stepCalls)
        )
    }

    private fun mapTestProperties(properties: TestProperties) = FileTestDefProperties(
            isManual = properties.isManual,
            isDisabled = properties.isDisabled
    )

}
