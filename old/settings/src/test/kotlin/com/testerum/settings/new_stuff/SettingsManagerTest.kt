package com.testerum.settings.new_stuff

import com.testerum.settings.SettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.model.Setting
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsManagerTest {

    private lateinit var settingsManager: SettingsManager

    @BeforeEach
    fun setUp() {
        settingsManager = SettingsManager()
    }

    @Test
    fun `register only definition`() {
        settingsManager.modify {
            registerDefinition(
                SettingDefinition(
                    key = "testerum.one",
                    label = "Testerum One",
                    type = SettingType.FILESYSTEM_DIRECTORY,
                    defaultValue = "/"
                )
            )
        }

        assertThat(settingsManager.getSettings())
            .isEqualTo(
                listOf(
                    Setting(
                        definition = SettingDefinition(
                            key = "testerum.one",
                            label = "Testerum One",
                            type = SettingType.FILESYSTEM_DIRECTORY,
                            defaultValue = "/"
                        ),
                        unresolvedValue = "/",
                        resolvedValue = "/"
                    )
                )
            )
    }

    @Test
    fun `register only value`() {
        settingsManager.modify {
            setValue("testerum.ref", "/changed/value")
        }

        assertThat(settingsManager.getSettings())
            .isEqualTo(
                listOf(
                    Setting(
                        definition = SettingDefinition(
                            key = "testerum.ref",
                            label = "testerum.ref",
                            type = SettingType.TEXT,
                            defaultValue = "",
                            defined = false
                        ),
                        unresolvedValue = "/changed/value",
                        resolvedValue = "/changed/value"
                    )
                )
            )
    }

    @Test
    fun `register both definition and value`() {
        settingsManager.modify {
            registerDefinition(
                SettingDefinition(
                    key = "testerum.one",
                    label = "Testerum One",
                    type = SettingType.FILESYSTEM_DIRECTORY,
                    defaultValue = "/"
                )
            )

            setValue("testerum.one", "/root{{testerum.ref}}")
            setValue("testerum.ref", "/changed/value")
        }

        assertThat(settingsManager.getSettings())
            .isEqualTo(
                listOf(
                    Setting(
                        definition = SettingDefinition(
                            key = "testerum.one",
                            label = "Testerum One",
                            type = SettingType.FILESYSTEM_DIRECTORY,
                            defaultValue = "/"
                        ),
                        unresolvedValue = "/root{{testerum.ref}}",
                        resolvedValue = "/root/changed/value"
                    ),
                    Setting(
                        definition = SettingDefinition(
                            key = "testerum.ref",
                            label = "testerum.ref",
                            type = SettingType.TEXT,
                            defaultValue = "",
                            defined = false
                        ),
                        unresolvedValue = "/changed/value",
                        resolvedValue = "/changed/value"
                    )
                )
            )
    }

}
