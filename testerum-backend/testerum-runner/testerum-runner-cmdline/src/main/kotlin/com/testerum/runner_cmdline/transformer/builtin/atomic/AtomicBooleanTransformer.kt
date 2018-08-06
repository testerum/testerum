package com.testerum.runner_cmdline.transformer.builtin.atomic

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.runner_cmdline.transformer.builtin.lang.BooleanTransformer
import java.util.concurrent.atomic.AtomicBoolean

class AtomicBooleanTransformer(private val booleanTransformer: BooleanTransformer) : Transformer<AtomicBoolean> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == AtomicBoolean::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): AtomicBoolean
            = AtomicBoolean(
                booleanTransformer.transform(toTransform, paramInfo)
            )

}
