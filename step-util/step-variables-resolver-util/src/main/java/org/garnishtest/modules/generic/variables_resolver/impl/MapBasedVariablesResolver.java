package org.garnishtest.modules.generic.variables_resolver.impl;

import org.garnishtest.modules.generic.variables_resolver.VariablesResolver;
import org.garnishtest.modules.generic.variables_resolver.exceptions.InvalidVariableNameException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnknownVariableException;
import org.garnishtest.modules.generic.variables_resolver.exceptions.UnterminatedVariableException;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;
import lombok.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;

@ThreadSafe
public final class MapBasedVariablesResolver implements VariablesResolver {

    @NonNull
    private final String prefix;
    @NonNull
    private final String suffix;

    @GuardedBy("itself")
    @NonNull
    private final Map<String, String> variableValues = new HashMap<>();

    public MapBasedVariablesResolver(@NonNull final String prefix,
                                     @NonNull final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void set(@NonNull final String name,
                    @Nullable final String value) throws InvalidVariableNameException {
        validateVariableName(name);

        synchronized (this.variableValues) {
            this.variableValues.put(name, String.valueOf(value));
        }
    }

    private void validateVariableName(@NonNull final String name) throws InvalidVariableNameException {
        final int indexOfPrefix = findPrefixIndex(name, 0);
        if (indexOfPrefix != -1) {
            throw new InvalidVariableNameException(
                    "variable names must not contain the prefix"
                    + ", but [" + name + "] contains [" + this.prefix + "]",
                    indexOfPrefix,
                    name
            );
        }

        final int indexOfSuffix = findSuffixIndex(name, 0);
        if (indexOfSuffix != -1) {
            throw new InvalidVariableNameException(
                    "variable names must not contain the suffix"
                    + ", but [" + name + "] contains [" + this.suffix + "]",
                    indexOfSuffix,
                    name
            );
        }
    }

    @Override
    @NonNull
    public String resolveVariablesInText(@Nonnull @NonNull final String textWithVariables,
                                         @Nonnull @NonNull final ValueEscaper valueEscaper)
            throws UnknownVariableException, UnterminatedVariableException {
        synchronized (this.variableValues) {
            final StringBuilder resolvedText = new StringBuilder();

            int lastCopiedPartIndex = 0;
            int prefixLastIndex;
            while ((prefixLastIndex = findPrefixIndex(textWithVariables, lastCopiedPartIndex)) != -1) {
                final int suffixIndex = findSuffixIndex(textWithVariables, prefixLastIndex);

                if (suffixIndex == -1) {
                    throw new UnterminatedVariableException(
                            "end of text reached"
                            + " while attempting to find the suffix [" + this.suffix + "]"
                            + " corresponding to prefix",
                            prefixLastIndex,
                            textWithVariables
                    );
                }

                final String variableValue = resolveVariableValue(
                        textWithVariables,
                        prefixLastIndex,
                        suffixIndex,
                        valueEscaper
                );

                // append previous non-variable text
                if (lastCopiedPartIndex < textWithVariables.length()) {
                    resolvedText.append(textWithVariables, lastCopiedPartIndex, prefixLastIndex);
                }

                // append variable value
                resolvedText.append(variableValue);

                // lastCopiedPartIndex is just past the suffix
                lastCopiedPartIndex = suffixIndex + this.suffix.length();
            }

            if (lastCopiedPartIndex < textWithVariables.length()) {
                resolvedText.append(textWithVariables, lastCopiedPartIndex, textWithVariables.length());
            }

            return resolvedText.toString();
        }
    }

    private int findPrefixIndex(@NonNull final String text, final int fromIndex) {
        return text.indexOf(this.prefix, fromIndex);
    }

    private int findSuffixIndex(@NonNull final String text, final int fromIndex) {
        return text.indexOf(this.suffix, fromIndex);
    }

    private String resolveVariableValue(@NonNull final String text,
                                        final int prefixLastIndex,
                                        final int suffixLastIndex,
                                        @NonNull final ValueEscaper valueEscaper) throws UnknownVariableException {
        final String variableName = text.substring(prefixLastIndex + this.prefix.length(), suffixLastIndex);

        // this is safe, since we only call this method from "resolveVariablesInText" and that already guards the read
        @SuppressWarnings("FieldAccessNotGuarded")
        final String variableValue = valueEscaper.escape(
                this.variableValues.get(variableName)
        );

        if (variableValue == null) {
            throw new UnknownVariableException("found unknown variable [" + variableName + "]", prefixLastIndex, text);
        }

        return variableValue;
    }

    @Override
    public String toString() {
        return "Variables{" +
               "prefix='" + prefix + '\'' +
               ", suffix='" + suffix + '\'' +
               ", variableValues=" + variableValues +
               '}';
    }

}
