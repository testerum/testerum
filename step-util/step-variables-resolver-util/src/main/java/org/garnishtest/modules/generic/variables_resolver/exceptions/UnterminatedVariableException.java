package org.garnishtest.modules.generic.variables_resolver.exceptions;

import lombok.NonNull;

public final class UnterminatedVariableException extends VariablesException {

    public UnterminatedVariableException(@NonNull final String errorMessage,
                                         final int errorIndex,
                                         @NonNull final String text) {
        super(errorMessage, errorIndex, text);
    }

}
