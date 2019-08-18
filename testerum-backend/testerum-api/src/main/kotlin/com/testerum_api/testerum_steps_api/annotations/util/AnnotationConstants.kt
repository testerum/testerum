package com.testerum_api.testerum_steps_api.annotations.util

import com.testerum_api.testerum_steps_api.annotations.util.AnnotationConstants.ANNOTATION_NULL

object AnnotationConstants {

    /**
     * work-around for the limitation that Java imposes that annotation fields can't be null
     */
    const val ANNOTATION_NULL = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n"

}

fun String.annotationNullToRealNull(): String? {
    return if (this == ANNOTATION_NULL) {
        null
    } else {
        this
    }
}
