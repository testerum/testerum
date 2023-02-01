package com.testerum.common_profiles

import org.slf4j.LoggerFactory

object TesterumProfileFinder {

    private val LOGGER = LoggerFactory.getLogger(TesterumProfileFinder::class.java)

    private const val SYSPROP_NAME = "testerum.profile"

    val currentProfile: TesterumProfile = getProfileFromSystemProperty()

    private fun getProfileFromSystemProperty(): TesterumProfile {
        val profileAsText = System.getProperty(SYSPROP_NAME)

        if (profileAsText == null) {
            LOGGER.info(
                    "the system property [$SYSPROP_NAME] is missing" +
                    "; will use the profile [${TesterumProfile.DEFAULT}]"
            )

            return TesterumProfile.DEFAULT
        }

        val testerumProfile = TesterumProfile.safeValueOf(profileAsText)

        if (testerumProfile == null) {
            LOGGER.warn(
                    "[$profileAsText] is an invalid value" +
                    " for the system property [$SYSPROP_NAME]" +
                    "; valid values are ${TesterumProfile.values().toList()}" +
                    "; will use the profile [${TesterumProfile.DEFAULT}]"
            )

            return TesterumProfile.DEFAULT
        }

        LOGGER.info(
                "will use the profile [$testerumProfile]"+
                " as specified in the the system property [$SYSPROP_NAME]"
        )

        return testerumProfile
    }

}
