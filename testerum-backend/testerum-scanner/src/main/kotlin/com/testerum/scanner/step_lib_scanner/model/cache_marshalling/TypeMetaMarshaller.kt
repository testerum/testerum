package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.BooleanTypeMeta
import com.testerum.model.text.parts.param_meta.DateTypeMeta
import com.testerum.model.text.parts.param_meta.EnumTypeMeta
import com.testerum.model.text.parts.param_meta.InstantTypeMeta
import com.testerum.model.text.parts.param_meta.ListTypeMeta
import com.testerum.model.text.parts.param_meta.LocalDateTimeTypeMeta
import com.testerum.model.text.parts.param_meta.LocalDateTypeMeta
import com.testerum.model.text.parts.param_meta.MapTypeMeta
import com.testerum.model.text.parts.param_meta.NumberTypeMeta
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.model.text.parts.param_meta.StringTypeMeta
import com.testerum.model.text.parts.param_meta.TypeMeta
import com.testerum.model.text.parts.param_meta.ZonedDateTimeTypeMeta

object TypeMetaMarshaller : FastMarshaller<TypeMeta> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: TypeMeta, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        when (data) {
            is StringTypeMeta -> {
                output.writeString("$prefix.@type", StringTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, StringTypeMetaMarshaller)
            }
            is BooleanTypeMeta -> {
                output.writeString("$prefix.@type", BooleanTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, BooleanTypeMetaMarshaller)
            }
            is NumberTypeMeta -> {
                output.writeString("$prefix.@type", NumberTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, NumberTypeMetaMarshaller)
            }
            is DateTypeMeta -> {
                output.writeString("$prefix.@type", DateTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, DateTypeMetaMarshaller)
            }
            is InstantTypeMeta -> {
                output.writeString("$prefix.@type", InstantTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, InstantTypeMetaMarshaller)
            }
            is LocalDateTypeMeta -> {
                output.writeString("$prefix.@type", LocalDateTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, LocalDateTypeMetaMarshaller)
            }
            is LocalDateTimeTypeMeta -> {
                output.writeString("$prefix.@type", LocalDateTimeTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, LocalDateTimeTypeMetaMarshaller)
            }
            is ZonedDateTimeTypeMeta -> {
                output.writeString("$prefix.@type", ZonedDateTimeTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, ZonedDateTimeTypeMetaMarshaller)
            }
            is EnumTypeMeta -> {
                output.writeString("$prefix.@type", EnumTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, EnumTypeMetaMarshaller)
            }
            is ListTypeMeta -> {
                output.writeString("$prefix.@type", ListTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, ListTypeMetaMarshaller)
            }
            is MapTypeMeta -> {
                output.writeString("$prefix.@type", MapTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, MapTypeMetaMarshaller)
            }
            is ObjectTypeMeta -> {
                output.writeString("$prefix.@type", ObjectTypeMetaMarshaller.TYPE)
                output.writeObject(prefix, data, ObjectTypeMetaMarshaller)
            }
            else -> throw RuntimeException("unknown type meta class [${data.javaClass}")
        }
    }

    override fun parse(prefix: String, input: FastInput): TypeMeta {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        return when (val type = input.readRequiredString("$prefix.@type")) {
            StringTypeMetaMarshaller.TYPE -> input.readObject(prefix, StringTypeMetaMarshaller)
            BooleanTypeMetaMarshaller.TYPE -> input.readObject(prefix, BooleanTypeMetaMarshaller)
            NumberTypeMetaMarshaller.TYPE -> input.readObject(prefix, NumberTypeMetaMarshaller)
            DateTypeMetaMarshaller.TYPE -> input.readObject(prefix, DateTypeMetaMarshaller)
            InstantTypeMetaMarshaller.TYPE -> input.readObject(prefix, InstantTypeMetaMarshaller)
            LocalDateTypeMetaMarshaller.TYPE -> input.readObject(prefix, LocalDateTypeMetaMarshaller)
            LocalDateTimeTypeMetaMarshaller.TYPE -> input.readObject(prefix, LocalDateTimeTypeMetaMarshaller)
            ZonedDateTimeTypeMetaMarshaller.TYPE -> input.readObject(prefix, ZonedDateTimeTypeMetaMarshaller)
            EnumTypeMetaMarshaller.TYPE -> input.readObject(prefix, EnumTypeMetaMarshaller)
            ListTypeMetaMarshaller.TYPE -> input.readObject(prefix, ListTypeMetaMarshaller)
            MapTypeMetaMarshaller.TYPE -> input.readObject(prefix, MapTypeMetaMarshaller)
            ObjectTypeMetaMarshaller.TYPE -> input.readObject(prefix, ObjectTypeMetaMarshaller)
            else -> throw RuntimeException("unknown type [$type]")
        }
    }

}
