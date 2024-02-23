package io.jdbd.util;

import io.jdbd.meta.BooleanMode;

public enum EscapeMode {
    DEFAULT(BooleanMode.TRUE),

    BACK_SLASH(BooleanMode.TRUE),

    UNICODE(BooleanMode.TRUE),

    DOLLAR_QUOTED(BooleanMode.TRUE),

    DEFAULT_NO_TYPE(BooleanMode.FALSE),

    BACK_SLASH_NO_TYPE(BooleanMode.FALSE),

    UNICODE_NO_TYPE(BooleanMode.FALSE),

    DOLLAR_QUOTED_NO_TYPE(BooleanMode.FALSE),

    ARRAY_ELEMENT(BooleanMode.UNKNOWN),

    ARRAY_ELEMENT_PART(BooleanMode.UNKNOWN);

    public final BooleanMode typeMode;

    EscapeMode(BooleanMode typeMode) {
        this.typeMode = typeMode;
    }


}
