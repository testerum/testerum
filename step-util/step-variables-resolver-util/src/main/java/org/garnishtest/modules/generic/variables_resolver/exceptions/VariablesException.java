package org.garnishtest.modules.generic.variables_resolver.exceptions;

import lombok.NonNull;

public class VariablesException extends RuntimeException {

    @NonNull private final String errorMessage;
    private final int errorIndex;
    @NonNull private final String text;

    @NonNull private final String fullErrorMessage;

    public VariablesException(@NonNull final String errorMessage,
                              final int errorIndex,
                              @NonNull final String text) {
        this.errorMessage = errorMessage;
        this.errorIndex = errorIndex;
        this.text = text;

        this.fullErrorMessage = createFullErrorMessage(errorMessage, errorIndex, text);
    }

    @NonNull
    private String createFullErrorMessage(@NonNull final String errorMessage,
                                          final int errorIndex,
                                          @NonNull final String text) {
        final StringBuilder result = new StringBuilder();

        result.append("\n");

        result.append(text).append("\n");

        // after snippet, error index is off
        for (int i = 0; i < errorIndex; i++) {
            result.append(' ');
        }
        result.append("^ ").append(errorMessage).append("; errorIndex=[").append(errorIndex).append("]");

        return result.toString();
    }

    public String getText() {
        return this.text;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorIndex() {
        return this.errorIndex;
    }

    @Override
    public String getMessage() {
        return this.fullErrorMessage;
    }

}
