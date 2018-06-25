package com.testerum.common_assertion_functions.executer

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.executer.arg_marshaller.FunctionArgMarshaller
import com.testerum.common_assertion_functions.functions.AssertionFunction
import com.testerum.common_assertion_functions.functions.USE_METHOD_NAME
import com.testerum.common_assertion_functions.parser.ast.*
import org.codehaus.commons.compiler.ISimpleCompiler
import org.codehaus.janino.SimpleCompiler
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.math.BigDecimal

object DelegatingFunctionExecuterFactory {

    @JvmStatic
    fun createDelegatingFunctionExecuter(functionObjects: List<Any>): DelegatingFunctionExecuter {
        val executers =  mutableMapOf<String, FunctionExecuter>()

        addExecutersForObjects(executers, functionObjects)

        return DelegatingFunctionExecuter(executers)
    }

    private fun addExecutersForObjects(destination: MutableMap<String, FunctionExecuter>,
                                       functionObjects: List<Any>) {
        for (functionObject in functionObjects) {
            addExecutersForObject(destination, functionObject)
        }
    }

    private fun addExecutersForObject(destination: MutableMap<String, FunctionExecuter>,
                                      functionObject: Any) {
        val functionClass: Class<Any> = functionObject.javaClass
        val functionMethods: Array<out Method> = functionClass.declaredMethods

        for (functionMethod in functionMethods) {
            addExecuterForMethod(destination, functionObject, functionMethod)
        }
    }

    private fun addExecuterForMethod(destination: MutableMap<String, FunctionExecuter>,
                                     functionObject: Any,
                                     functionMethod: Method) {
        if (!functionMethod.isAssertionFunction()) {
            return
        }

        functionMethod.validateAssertionFunction()
        validateNonExistingExecuter(destination, functionMethod)

        destination.put(
                functionMethod.assertionFunctionName,
                createExecuterForMethod(functionMethod, functionObject)
        )
    }

    private fun Method.isAssertionFunction(): Boolean
            = this.getAnnotation(AssertionFunction::class.java) != null

    private val Method.assertionFunctionName
        get(): String {
            val annotation = this.getAnnotation(AssertionFunction::class.java)
            if (annotation == null) {
                throw IllegalArgumentException("cannot find assertion function name for [$this]: the method should be annotated with @${AssertionFunction::class.java.simpleName}")
            }

            val nameFromAnnotation = annotation.functionName
            if (nameFromAnnotation == USE_METHOD_NAME) {
                return this.name
            } else {
                return nameFromAnnotation
            }
        }

    private fun Method.validateAssertionFunction() {
        val errorPrefix = "cannot create executer for [$this]"

        val annotation = this.getAnnotation(AssertionFunction::class.java)
        if (annotation == null) {
            throw IllegalArgumentException("$errorPrefix: the method must be annotated with @${AssertionFunction::class.java.simpleName}")
        }

        if (!Modifier.isPublic(this.modifiers)) {
            throw IllegalArgumentException("$errorPrefix: the method must be public")
        }

        if (Modifier.isStatic(this.modifiers)) {
            throw IllegalArgumentException("$errorPrefix: the method must not be static")
        }

        if (Modifier.isAbstract(this.modifiers)) {
            throw IllegalArgumentException("$errorPrefix: the method must not be abstract")
        }

        if (this.returnType != Void.TYPE) {
            throw IllegalArgumentException("$errorPrefix: the method must return void")
        }

        val parameters: Array<out Parameter> = this.parameters
        if (parameters.isEmpty()) {
            throw IllegalArgumentException(
                    "$errorPrefix: the method must have at least one parameter - the one that receives the actual value (and it must be of type ${JsonNode::class.java.name})"
            )
        }

        val firstParameter: Parameter = parameters[0]
        if (firstParameter.type != JsonNode::class.java) {
            throw IllegalArgumentException(
                    "$errorPrefix: method must have the first parameter of type [${JsonNode::class.java.name}]"
            )
        }
    }

    private fun validateNonExistingExecuter(destination: MutableMap<String, FunctionExecuter>, functionMethod: Method): String {
        val functionName: String = functionMethod.assertionFunctionName

        val existingExecuter: FunctionExecuter? = destination[functionName]
        if (existingExecuter != null) {
            throw IllegalStateException(
                    "cannot create executer for function [$functionName] from source [$functionMethod]" +
                    " because there is already another executer for this function created from source [${existingExecuter.source}]")
        }

        return functionName
    }

    private fun createExecuterForMethod(functionMethod: Method,
                                        functionObject: Any): FunctionExecuter {
        try {
            val executerClass: Class<*> = generateExecuterClass(functionMethod)
            val executer: FunctionExecuter = instantiateExecuter(executerClass, functionObject)

            return executer
        } catch (e: Exception) {
            throw IllegalArgumentException("failed to create executer for method [$functionMethod]", e)
        }
    }

    private fun generateExecuterClass(functionMethod: Method): Class<*> {
        val packageName = "${FunctionExecuter::class.java.`package`.name}.generated_impl"
        val executerSimpleClassName = "\$GeneratedFunctionExecuter_for_${functionMethod.assertionFunctionName}"

        val generatedExecuterClassSource = generateExecuterClassSourceCode(functionMethod, packageName, executerSimpleClassName)

        val compiler: ISimpleCompiler = SimpleCompiler()
        try {
            compiler.cook(generatedExecuterClassSource)
        } catch (e: Exception) {
            throw RuntimeException(
                    "failed to compile generated class:\n" +
                    "-------------------------------------------------\n" +
                    "${generatedExecuterClassSource.withLineNumbers()}\n" +
                    "-------------------------------------------------\n",
                    e
            )
        }

        val executerFullyQualifiedClassName = "${packageName}.${executerSimpleClassName}";
        val functionExecuterClass: Class<*> = compiler.classLoader.loadClass(executerFullyQualifiedClassName)

        return functionExecuterClass
    }

    private fun generateExecuterClassSourceCode(functionMethod: Method,
                                                packageName: String,
                                                executerSimpleClassName: String): String {
        val functionClass: Class<*> = functionMethod.declaringClass

        val argumentValues = mutableListOf<String>()
        for ((index, parameter) in functionMethod.parameters.withIndex()) {
            if (index == 0) {
                argumentValues += "actualNode"
            } else {
                argumentValues += when (parameter.type) {
                    java.lang.Boolean.TYPE           -> "argToBooleanPrimitive((FunctionArgument) arguments.get(${index - 1}), $index)"
                    java.lang.Boolean::class.java    -> "argToBooleanObject((FunctionArgument) arguments.get(${index - 1}), $index)"
                    java.lang.Integer.TYPE           -> "argToIntegerPrimitive((FunctionArgument) arguments.get(${index - 1}), $index)"
                    java.lang.Integer::class.java    -> "argToIntegerObject((FunctionArgument) arguments.get(${index - 1}), $index)"
                    BigDecimal::class.java           -> "argToBigDecimal((FunctionArgument) arguments.get(${index - 1}), $index)"
                    String::class.java               -> "argToString((FunctionArgument) arguments.get(${index - 1}), $index)"
                    else                             -> throw IllegalArgumentException("parameter number ${index + 1} of method [$functionMethod] is of unsupported type [${parameter.type.name}]")
                }
            }
        }

        return  """
                |package $packageName;
                |
                |import ${List::class.java.name};
                |
                |import ${JsonNode::class.java.name};
                |
                |import ${FunctionExecuter::class.java.name};
                |import ${FunctionArgument::class.java.name};
                |import ${NullFunctionArgument::class.java.name};
                |import ${BooleanFunctionArgument::class.java.name};
                |import ${IntegerFunctionArgument::class.java.name};
                |import ${TextFunctionArgument::class.java.name};
                |
                |import ${functionClass.name};
                |
                |import static ${FunctionArgMarshaller.javaClass.name}.*;
                |
                |public final class $executerSimpleClassName implements ${FunctionExecuter::class.java.simpleName} {
                |    private final ${functionClass.simpleName} functionObject;
                |
                |    public $executerSimpleClassName(final ${functionClass.simpleName} functionObject) {
                |        this.functionObject = functionObject;
                |    }
                |
                |    public String getSource() {
                |       return "${functionMethod}";
                |    }
                |
                |    public void execute(final JsonNode actualNode, final List<FunctionArgument> arguments) {
                |        if (arguments.size() != ${argumentValues.size - 1}) {
                |            throw new IllegalArgumentException("function [${functionMethod.name}] expects ${argumentValues.size -1} arguments, but got " + arguments.size());
                |        }
                |
                |        functionObject.${functionMethod.name}(
                |            ${argumentValues.joinToString(separator = ",\n            ")}
                |        );
                |    }
                |
                |}
                """.trimMargin()
    }

    private fun String.withLineNumbers()
            = this.lineSequence()
            .withIndex()
            .map { (index, line) -> "${(index + 1).toString().padStart(length = 4)}. $line" }
            .joinToString(separator = "\n")

    private fun instantiateExecuter(executerClass: Class<*>,
                                    functionObject: Any): FunctionExecuter {
        // passing an instance of the function object to the generated instance (will be used for delegation)
        val constructor: Constructor<out Any> = executerClass.getDeclaredConstructor(functionObject::class.java)
        val functionExecuter: FunctionExecuter = constructor.newInstance(functionObject) as FunctionExecuter

        return functionExecuter
    }

}
