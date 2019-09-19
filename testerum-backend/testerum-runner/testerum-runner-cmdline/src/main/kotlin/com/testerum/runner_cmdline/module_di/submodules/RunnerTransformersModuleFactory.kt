package com.testerum.runner_cmdline.module_di.submodules

import com.testerum.api.transformer.Transformer
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.runner_cmdline.transformer.builtin.atomic.AtomicBooleanTransformer
import com.testerum.runner_cmdline.transformer.builtin.atomic.AtomicIntegerTransformer
import com.testerum.runner_cmdline.transformer.builtin.atomic.AtomicLongTransformer
import com.testerum.runner_cmdline.transformer.builtin.enums.EnumTransformer
import com.testerum.runner_cmdline.transformer.builtin.lang.*
import com.testerum.runner_cmdline.transformer.builtin.math.BigDecimalTransformer
import com.testerum.runner_cmdline.transformer.builtin.math.BigIntegerTransformer

class RunnerTransformersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val globalTransformers: List<Transformer<*>> = listOf(
            BooleanTransformer,
            CharacterTransformer,
            ByteTransformer,
            ShortTransformer,
            IntegerTransformer,
            LongTransformer,
            FloatTransformer,
            DoubleTransformer,
            BigIntegerTransformer,
            BigDecimalTransformer,
            AtomicBooleanTransformer(BooleanTransformer),
            AtomicIntegerTransformer(IntegerTransformer),
            AtomicLongTransformer(LongTransformer),
            EnumTransformer
    )
}