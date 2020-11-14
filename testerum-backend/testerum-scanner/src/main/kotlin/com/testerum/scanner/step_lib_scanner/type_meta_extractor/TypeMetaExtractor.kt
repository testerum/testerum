package com.testerum.scanner.step_lib_scanner.type_meta_extractor

import com.testerum.model.text.parts.param_meta.TypeMeta
import com.testerum.model.text.parts.param_meta.TypeMetaFactory
import java.lang.reflect.Method
import java.lang.reflect.Parameter

object TypeMetaExtractor {

    fun extractTypeMetaFromParameter(param: Parameter): TypeMeta {
        return TypeMetaFactory.getTypeMetaFromJavaType(param.type, param.parameterizedType)
    }

    fun extractTypeMetaFromMethodResult(method: Method): TypeMeta {
        return TypeMetaFactory.getTypeMetaFromJavaType(method.returnType, method.genericReturnType)
    }
}
