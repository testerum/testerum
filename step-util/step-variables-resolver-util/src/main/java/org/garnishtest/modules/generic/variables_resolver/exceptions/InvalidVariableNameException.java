package org.garnishtest.modules.generic.variables_resolver.exceptions;

import lombok.NonNull;

public final class InvalidVariableNameException extends VariablesException {

    public InvalidVariableNameException(@NonNull final String errorMessage,
                                        final int errorIndex,
                                        @NonNull final String text) {
        super(errorMessage, errorIndex, text);
    }

}
