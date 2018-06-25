package org.garnishtest.modules.generic.variables_resolver.impl.escape.impl;

import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;

import javax.annotation.Nullable;

final class NoValueEscaper implements ValueEscaper {

    @Nullable
    @Override
    public String escape(@Nullable final String textToEscape) {
        return textToEscape;
    }

}
