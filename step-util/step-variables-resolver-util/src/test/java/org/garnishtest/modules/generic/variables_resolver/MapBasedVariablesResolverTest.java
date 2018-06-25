package org.garnishtest.modules.generic.variables_resolver;

import org.garnishtest.modules.generic.variables_resolver.exceptions.InvalidVariableNameException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnknownVariableException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnterminatedVariableException;
import org.garnishtest.modules.generic.variables_resolver.impl.MapBasedVariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.impl.ValueEscapers;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapBasedVariablesResolverTest {

    private static final String VAR_PREFIX = "${";
    private static final String VAR_SUFFIX = "}";

    @Test
    public void should_not_change_text_if_there_are_no_variables_to_resolve() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);

        assertEquals(
                "not changed",
                variables.resolveVariablesInText("not changed", ValueEscapers.none())
        );
    }

    @Test
    public void should_resolve_all_variables() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);
        variables.set("user"           , "John");
        variables.set("className"      , "Variables");
        variables.set("finalSalutation", "Have a wonderful day");

        assertEquals(
                "Hi John! Variables welcomes you, John. Variables is very happy to meet John. Have a wonderful day!",
                variables.resolveVariablesInText("Hi ${user}! ${className} welcomes you, ${user}. ${className} is very happy to meet ${user}. ${finalSalutation}!", ValueEscapers.none())
        );
    }

    @Test
    public void should_resolve_all_variables_with_custom_prefix_and_suffix() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver("<<", ">>");
        variables.set("user"           , "John");
        variables.set("className"      , "Variables");
        variables.set("finalSalutation", "Have a wonderful day");

        assertEquals(
                "Hi John! Variables welcomes you, John. Variables is very happy to meet John. Have a wonderful day!",
                variables.resolveVariablesInText("Hi <<user>>! <<className>> welcomes you, <<user>>. <<className>> is very happy to meet <<user>>. <<finalSalutation>>!", ValueEscapers.none())
        );
    }

    @Test
    public void should_not_resolve_variables_recursively_in_order() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);
        variables.set("var_1", "${var_2}");
        variables.set("var_2", "val 2");

        assertEquals(
                "${var_2}",
                variables.resolveVariablesInText("${var_1}", ValueEscapers.none())
        );
    }

    @Test
    public void should_not_resolve_variables_recursively_reverse_order() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);
        variables.set("var_2", "val 2");
        variables.set("var_1", "${var_2}");

        assertEquals(
                "${var_2}",
                variables.resolveVariablesInText("${var_1}", ValueEscapers.none())
        );
    }

    @Test
    public void should_invoke_values_escaper() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);
        variables.set("comparison", "3 > 2");

        final ValueEscaper valueEscaper = new ValueEscaper() {
            @Nullable
            @Override
            public String escape(@Nullable final String textToEscape) {
                if (textToEscape == null) {
                    return null;
                }

                return textToEscape.replaceAll(">", "greater than");
            }
        };

        assertEquals(
                "The comparison is: [3 greater than 2]",
                variables.resolveVariablesInText("The comparison is: [${comparison}]", valueEscaper)
        );

    }

    @Test
    public void should_throw_exception_if_we_set_a_variable_whose_name_contains_prefix() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);

        assertThrows(
                InvalidVariableNameException.class,
                () -> variables.set("the ${var", "value")
        );
    }

    @Test
    public void should_throw_exception_if_we_set_a_variable_whose_name_contains_suffix() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);

        assertThrows(
                InvalidVariableNameException.class,
                () -> variables.set("the } var", "value")
        );
    }

    @Test
    public void should_throw_exception_for_unknown_variable_if_we_have_variables() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);
        variables.set("var", "val");

        assertThrows(
                UnknownVariableException.class,
                () -> variables.resolveVariablesInText("this ${variable} is not resolvable", ValueEscapers.none())
        );
    }

    @Test
    public void should_throw_exception_for_unknown_variable_if_we_do_not_have_variables() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);

        assertThrows(
                UnknownVariableException.class,
                () -> variables.resolveVariablesInText("this ${variable} is not resolvable", ValueEscapers.none())
        );
    }

    @Test
    public void should_not_allow_unterminated_variables() throws Exception {
        final MapBasedVariablesResolver variables = new MapBasedVariablesResolver(VAR_PREFIX, VAR_SUFFIX);

        assertThrows(
                UnterminatedVariableException.class,
                () -> variables.resolveVariablesInText("this ${variable has no suffix", ValueEscapers.none())
        );
    }
}