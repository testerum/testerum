package org.garnishtest.modules.generic.variables_resolver;

import org.garnishtest.modules.generic.variables_resolver.exceptions.UnknownVariableException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnterminatedVariableException;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;
import lombok.NonNull;

import javax.annotation.Nonnull;

public interface VariablesResolver {

    @NonNull
    String resolveVariablesInText(@Nonnull  String textWithVariables,
                                  @Nonnull final ValueEscaper valueEscaper) throws UnknownVariableException,
                                                                                   UnterminatedVariableException;

}
