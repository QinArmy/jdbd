package io.jdbd.type.geometry;

import io.jdbd.type.LongBinary;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface LongGenericGeometry extends GenericGeometry, LongBinary {


    /**
     * @return WKB bytes
     */
    @Override
    byte[] asArray() throws IllegalStateException;

    /**
     * @return WKB bytes {@link FileChannel} of underlying file.
     */
    @Override
    FileChannel openReadOnlyChannel() throws IOException, IllegalStateException;


    /**
     * <p>
     * if {@link #isArray()}  is true ,return full upper case wkt, or upper case wkt omitted suffix part.
     * </p>
     *
     * @return wkt or wkt omitted suffix part
     */
    String toWkt();

    /**
     * @return always same with {@link Object#hashCode()}, because maybe too long and driver shouldn't do this.
     */
    @Override
    int hashCode();

    /**
     * @return always same with {@link Object#equals(Object)}, because maybe too long and driver shouldn't do this.
     */
    @Override
    boolean equals(Object obj);

}