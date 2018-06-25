package org.garnishtest.modules.generic.variables_resolver.impl.escape.impl;

import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.annotation.Nullable;

final class JsonValueEscaper implements ValueEscaper {

    @Nullable
    @Override
    public String escape(@Nullable final String textToEscape) {
        if (textToEscape == null) {
            return null;
        }

        return StringEscapeUtils.escapeJson(textToEscape);
    }

}
