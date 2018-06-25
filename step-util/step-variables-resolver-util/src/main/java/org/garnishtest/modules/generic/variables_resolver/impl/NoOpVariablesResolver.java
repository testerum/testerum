package org.garnishtest.modules.generic.variables_resolver.impl;

import org.garnishtest.modules.generic.variables_resolver.VariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnknownVariableException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnterminatedVariableException;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;
import lombok.NonNull;

import javax.annotation.Nonnull;

public final class NoOpVariablesResolver implements VariablesResolver {

    private static final NoOpVariablesResolver INSTANCE = new NoOpVariablesResolver();

    private NoOpVariablesResolver() { }

    public static NoOpVariablesResolver instance() {
        return INSTANCE;
    }

    @Override
    public String resolveVariablesInText(@Nonnull @NonNull final String textWithVariables,
                                         @Nonnull @NonNull final ValueEscaper valueEscaper)
            throws UnknownVariableException, UnterminatedVariableException {
        return textWithVariables;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
