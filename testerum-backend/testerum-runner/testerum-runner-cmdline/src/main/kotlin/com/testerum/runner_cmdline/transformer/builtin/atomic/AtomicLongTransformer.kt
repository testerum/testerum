package com.testerum.runner_cmdline.transformer.builtin.atomic

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.runner_cmdline.transformer.builtin.lang.LongTransformer
import java.util.concurrent.atomic.AtomicLong

class AtomicLongTransformer(private val longTransformer: LongTransformer) : Transformer<AtomicLong> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == AtomicLong::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): AtomicLong
            = AtomicLong(
                longTransformer.transform(toTransform, paramInfo)
            )

}
