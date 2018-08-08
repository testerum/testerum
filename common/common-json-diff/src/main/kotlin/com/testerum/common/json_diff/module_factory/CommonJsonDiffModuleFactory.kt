package com.testerum.common.json_diff.module_factory

import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.JsonComparerImpl
import com.testerum.common.json_diff.impl.compare_mode.JsonCompareMode
import com.testerum.common.json_diff.impl.node_comparer.JsonNodeComparer
import com.testerum.common.json_diff.impl.node_comparer.strategy.impl.ContainsJsonNodeComparerStrategy
import com.testerum.common.json_diff.impl.node_comparer.strategy.impl.ExactJsonNodeComparerStrategy
import com.testerum.common.json_diff.impl.node_comparer.strategy.impl.UnorderedExactJsonNodeComparerStrategy
import com.testerum.common_assertion_functions.module_factory.CommonAssertionFunctionsModuleFactory
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext

@Suppress("unused", "LeakingThis")
class CommonJsonDiffModuleFactory(context: ModuleFactoryContext,
                                  commonAssertionFunctionsModuleFactory: CommonAssertionFunctionsModuleFactory) : BaseModuleFactory(context) {

    val jsonNodeComparer: JsonNodeComparer = JsonNodeComparer(
            functionEvaluator = commonAssertionFunctionsModuleFactory.functionEvaluator,
            comparerStrategies = mapOf(
                    JsonCompareMode.EXACT           to ExactJsonNodeComparerStrategy,
                    JsonCompareMode.UNORDERED_EXACT to UnorderedExactJsonNodeComparerStrategy,
                    JsonCompareMode.CONTAINS        to ContainsJsonNodeComparerStrategy
            )
    )

    val jsonComparer: JsonComparer = JsonComparerImpl(jsonNodeComparer)

}
