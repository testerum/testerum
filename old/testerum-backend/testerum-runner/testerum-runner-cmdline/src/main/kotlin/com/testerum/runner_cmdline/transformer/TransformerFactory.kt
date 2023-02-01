package com.testerum.runner_cmdline.transformer

import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.transformer.builtin.unknonw_object.ObjectTransformer
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import com.testerum_api.testerum_steps_api.transformer.impl.NoTransformer

class TransformerFactory(private val glueObjectFactory: GlueObjectFactory,
                         private val globalTransformers: List<Transformer<*>>) {

    // todo: integration tests
    // for all: including failures
    // String: non-null / {null}
    // Boolean: primitive: true/false/yes/no; object: true/false/yes/no/{null}
    // char, Character, byte, Byte, short, Short, int, Integer, long, Long, float, Float, double, Double: primitives: all boundaries (min/max); objects: all for primitives + {null}

    // todo: transformers for:
    // AtomicReference
    // Date, Calendar, java8 date-time (how to specify the format?)
    // collections (list, set, map, etc - use methodParam.parametrizedType)
    // arrays, AtomicIntegerArray, AtomicLongArray, AtomicReferenceArray

    // todo: look at how cucumber implemented transformers and see what we are missing

    fun getTransformer(paramInfo: ParameterInfo): Transformer<Any?>? {
        return getPerParameterTransformer(paramInfo)
                ?: getGlobalTransformer(paramInfo)
                ?: getDefaultTransformer()
    }

    private fun getPerParameterTransformer(paramInfo: ParameterInfo): Transformer<Any?>? {
        val transformerClass: Class<out Transformer<Any?>> = paramInfo.transformerClass
                ?: return null

        if (transformerClass == NoTransformer::class.java) {
            return null
        }

        // todo: if not found in GlueObjectFactory, instantiate using default constructor (todo: in this case, where to store the transformers? maybe we need a new getInstance() method that falls-back to instantiating using the default constructor)
        return glueObjectFactory.getInstance(transformerClass)
    }

    // todo: performance tip: cache the response of this method; key=paramInfo
    private fun getGlobalTransformer(paramInfo: ParameterInfo): Transformer<Any?>? {
        return globalTransformers.firstOrNull { it.canTransform(paramInfo) }
    }

    private fun getDefaultTransformer(): Transformer<Any?> {
        return ObjectTransformer
    }
}
