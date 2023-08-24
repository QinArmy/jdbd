package io.jdbd.type;


import io.jdbd.result.BigColumnValue;

import java.nio.file.Path;

public interface BlobPath extends PathParameter, BigColumnValue {


    static BlobPath from(boolean deleteOnClose, Path path) {
        return JdbdTypes.blobPathParam(deleteOnClose, path);
    }


}
