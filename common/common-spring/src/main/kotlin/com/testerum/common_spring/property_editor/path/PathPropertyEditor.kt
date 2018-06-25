package com.testerum.common_spring.property_editor.path

import java.beans.PropertyEditorSupport
import java.nio.file.Paths

class PathPropertyEditor : PropertyEditorSupport() {

    override fun setAsText(text: String) {
        value = Paths.get(text)
    }

}
