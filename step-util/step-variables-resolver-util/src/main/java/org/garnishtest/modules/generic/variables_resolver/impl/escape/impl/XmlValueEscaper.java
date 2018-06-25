package org.garnishtest.modules.generic.variables_resolver.impl.escape.impl;

import com.google.common.xml.XmlEscapers;
import org.garnishtest.modules.generic.variables_resolver.impl.escape.ValueEscaper;

import javax.annotation.Nullable;

final class XmlValueEscaper implements ValueEscaper {

    @Nullable
    @Override
    public String escape(@Nullable final String textToEscape) {
        if (textToEscape == null) {
            return null;
        }

        return XmlEscapers.xmlAttributeEscaper().escape(textToEscape);
    }

}
