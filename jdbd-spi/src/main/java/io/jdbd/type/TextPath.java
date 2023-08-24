package io.jdbd.type;


import io.jdbd.result.BigColumnValue;

import java.nio.charset.Charset;
import java.nio.file.Path;

public interface TextPath extends PathParameter, BigColumnValue {

    Charset charset();


    static TextPath from(boolean deleteOnClose, Charset charset, Path path) {
        return JdbdTypes.textPathParam(deleteOnClose, charset, path);
    }

}
