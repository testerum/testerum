package org.garnishtest.modules.generic.variables_resolver.impl.escape;

import javax.annotation.Nullable;

public interface ValueEscaper {

    @Nullable
    String escape(@Nullable String textToEscape);

}
