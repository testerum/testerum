package com.testerum.scanner.step_lib_scanner.type_meta_extractor

import com.testerum.model.text.parts.param_meta.TypeMeta
import com.testerum.model.text.parts.param_meta.TypeMetaFactory
import java.lang.reflect.Parameter

object TypeMetaExtractor {

    fun extractTypeMeta(param: Parameter): TypeMeta {

        // todo: move this to a separate method


        return TypeMetaFactory.getTypeMetaFromJavaType(param.type, param.parameterizedType)
    }
}