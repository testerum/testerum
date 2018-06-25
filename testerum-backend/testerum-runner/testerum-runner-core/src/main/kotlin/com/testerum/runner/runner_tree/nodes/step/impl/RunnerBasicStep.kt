package com.testerum.runner.runner_tree.nodes.step.impl

import com.testerum.api.annotations.steps.Param
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.runner.runner_tree.nodes.step.RunnerStep
import com.testerum.runner.runner_tree.runner_context.RunnerContext
import com.testerum.runner.runner_tree.vars_context.VariablesContext
import com.testerum.runner.transformer.TransformerFactory
import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.StepCall
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.StepPatternPart
import org.springframework.util.ClassUtils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class RunnerBasicStep(stepCall: StepCall,
                      indexInParent: Int) : RunnerStep(stepCall, indexInParent) {

    private val stepDef: BasicStepDef = stepCall.stepDef as? BasicStepDef
            ?: throw IllegalArgumentException("this step call is not a basic step")

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val glueClass: Class<*> = try {
            context.stepsClassLoader.loadClass(stepDef.className)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("failed to load glue class [${stepDef.className}]", e)
        }

        return listOf(glueClass)
    }

    override fun doRun(context: RunnerContext, vars: VariablesContext): ExecutionStatus {
        val stepClass: Class<*> = try {
            context.stepsClassLoader.loadClass(stepDef.className)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("failed to load glue class [${stepDef.className}]", e)
        }

        val stepParamsTypes: List<Class<*>> = stepDef.stepPattern.patternParts.toParamsTypes(context.stepsClassLoader)
        val stepMethod: Method = stepClass.getMethod(stepDef.methodName, *stepParamsTypes.toTypedArray())

        val stepInstance: Any = context.glueObjectFactory.getInstance(stepClass)

        val untransformedStepMethodArguments: List<Any?> = stepCall.args.map { vars.resolveIn(it) }

        val stepMethodArguments: List<Any?> = transformMethodArguments(untransformedStepMethodArguments, stepMethod, context.transformerFactory)

        try {
            stepMethod.invoke(stepInstance, *stepMethodArguments.toTypedArray())

            return ExecutionStatus.PASSED
        } catch (e: InvocationTargetException) {
            throw e.cause!!
        }
    }

    private fun List<StepPatternPart>.toParamsTypes(stepsClassLoader: ClassLoader): List<Class<*>> {
        val result = mutableListOf<Class<*>>()

        for (patternPart in this) {
            if (patternPart !is ParamStepPatternPart) {
                continue
            }

            val paramClass: Class<*> = ClassUtils.forName(patternPart.type, stepsClassLoader)

            result += paramClass
        }

        return result
    }

    private fun transformMethodArguments(untransformedArgs: List<Any?>,
                                         stepMethod: Method,
                                         transformerFactory: TransformerFactory): List<Any?> {
        val result = mutableListOf<Any?>()

        val params: Array<Parameter> = stepMethod.parameters

        for (i in 0 until params.size) {
            val param = params[i]
            try {
                val untransformedArg: Any? = untransformedArgs[i]

                val paramInfo = ParameterInfo(
                        type             = param.type,
                        parametrizedType = param.parameterizedType,
                        transformerClass = param.getAnnotation(Param::class.java)?.transformer?.java
                )
                val transformer: Transformer<Any?>? = transformerFactory.getTransformer(paramInfo)

                val transformedArg: Any? = transformArgument(untransformedArg, transformer, paramInfo)

                result.add(transformedArg)
            } catch (e: Exception) {
                throw RuntimeException("failed to prepare value for parameter [${param.name}] of method [$stepMethod]", e)
            }
        }

        return result
    }

    private fun transformArgument(untransformedArg: Any?,
                                  transformer: Transformer<*>?,
                                  paramInfo: ParameterInfo): Any? {
        try {
            if (untransformedArg == null) {
                if (paramInfo.type.isPrimitive) {
                    throw RuntimeException("cannot assign null to a primitive")
                } else {
                    return null
                }
            }

            val untransformedType: Class<Any> = untransformedArg.javaClass
            if (paramInfo.type.isAssignableFrom(untransformedType)) {
                return untransformedArg
            }

            // we can only convert String-s
            val mismatchedArgTypesErrorMessage = "mismatched argument types: the step method expects [${paramInfo.type.name}], but the actual value is [${untransformedType.name}]"
            val untransformedArgAsString: String = untransformedArg as? String
                    ?: throw RuntimeException(mismatchedArgTypesErrorMessage)

            if (transformer == null) {
                throw RuntimeException("$mismatchedArgTypesErrorMessage; in addition, no Transformer<${paramInfo.type.simpleName}> could be found")
            }

            return transformer.transform(
                    toTransform = untransformedArgAsString,
                    paramInfo = paramInfo
            )
        } catch (e: Exception) {
            throw RuntimeException("failed to run transformer [${transformer?.javaClass}]", e)
        }
    }

}
