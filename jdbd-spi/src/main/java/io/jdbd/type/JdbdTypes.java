package io.jdbd.type;

import io.jdbd.JdbdException;
import io.jdbd.lang.NonNull;
import io.jdbd.lang.Nullable;
import org.reactivestreams.Publisher;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

abstract class JdbdTypes {

    private JdbdTypes() {
        throw new UnsupportedOperationException();
    }

    static Blob blobParam(@Nullable Publisher<byte[]> source) {
        if (source == null) {
            throw new NullPointerException("source must non-null");
        }
        return new JdbdBlob(source);
    }

    @SuppressWarnings("unchecked")
    static <T extends CharSequence> Clob clobParam(@Nullable Publisher<T> source) {
        if (source == null) {
            throw new NullPointerException("source must non-null");
        }
        return new JdbdClob((Publisher<CharSequence>) source);
    }

    static Text textParam(@Nullable Charset charset, @Nullable Publisher<byte[]> source) {
        if (charset == null) {
            throw new NullPointerException("charset must non-null");
        } else if (source == null) {
            throw new NullPointerException("source must non-null");
        }
        return new JdbdText(charset, source);
    }

    static TextPath textPathParam(boolean deleteOnClose, @Nullable Charset charset, @Nullable Path path) {
        if (charset == null) {
            throw new NullPointerException("charset must non-null");
        } else if (path == null) {
            throw new NullPointerException("path must non-null");
        }
        return new JdbdTextPath(deleteOnClose, charset, path);
    }

    static BlobPath blobPathParam(boolean deleteOnClose, @Nullable Path path) {
        if (path == null) {
            throw new NullPointerException("path must non-null");
        }
        return new JdbdBlobPath(deleteOnClose, path);
    }


    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    static NullPointerException fluxFuncIsNull() {
        return new NullPointerException("fluxFunc must be non-null");
    }

    /**
     * @return {@link NullPointerException} not {@link JdbdException}
     */
    static NullPointerException fluxFuncReturnNull(Function<?, ?> fluxFunc) {
        return new NullPointerException(String.format("%s must return non-null", fluxFunc));
    }


    private static final class JdbdBlob implements Blob {

        private final Publisher<byte[]> source;

        private JdbdBlob(Publisher<byte[]> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public Publisher<byte[]> value() {
            return this.source;
        }

        @Override
        public <F extends Publisher<byte[]>> F value(final @Nullable Function<Publisher<byte[]>, F> fluxFunc) {
            if (fluxFunc == null) {
                throw fluxFuncIsNull();
            }
            final F flux;
            flux = fluxFunc.apply(this.source);
            if (flux == null) {
                throw fluxFuncReturnNull(fluxFunc);
            }
            return flux;
        }

    }//JdbdBlob

    private static final class JdbdClob implements Clob {

        private final Publisher<CharSequence> source;

        private JdbdClob(Publisher<CharSequence> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public Publisher<CharSequence> value() {
            return this.source;
        }

        @Override
        public <F extends Publisher<CharSequence>> F value(final @Nullable Function<Publisher<CharSequence>, F> fluxFunc) {
            if (fluxFunc == null) {
                throw fluxFuncIsNull();
            }
            final F flux;
            flux = fluxFunc.apply(this.source);
            if (flux == null) {
                throw fluxFuncReturnNull(fluxFunc);
            }
            return flux;
        }

    }//JdbdBlob

    private static final class JdbdText implements Text {

        private final Charset charset;

        private final Publisher<byte[]> source;

        private JdbdText(Charset charset, Publisher<byte[]> source) {
            this.charset = charset;
            this.source = source;
        }

        @Override
        public Charset charset() {
            return this.charset;
        }


        @NonNull
        @Override
        public Publisher<byte[]> value() {
            return this.source;
        }

        @Override
        public <F extends Publisher<byte[]>> F value(final @Nullable Function<Publisher<byte[]>, F> fluxFunc) {
            if (fluxFunc == null) {
                throw fluxFuncIsNull();
            }
            final F flux;
            flux = fluxFunc.apply(this.source);
            if (flux == null) {
                throw fluxFuncReturnNull(fluxFunc);
            }
            return flux;
        }

        @Override
        public String toString() {
            return String.format("%s[ charset : %s ]", getClass().getName(), this.charset.name());
        }


    }//JdbdText

    private static final class JdbdTextPath implements TextPath {

        private final boolean deleteOnClose;

        private final Charset charset;

        private final Path path;

        private JdbdTextPath(boolean deleteOnClose, Charset charset, Path path) {
            this.deleteOnClose = deleteOnClose;
            this.charset = charset;
            this.path = path;
        }

        @Override
        public Charset charset() {
            return this.charset;
        }

        @Override
        public boolean isDeleteOnClose() {
            return this.deleteOnClose;
        }

        @NonNull
        @Override
        public Path value() {
            return this.path;
        }

        @Override
        public String toString() {
            return String.format("%s[ deleteOnClose : %s , charset : %s , path : %s]",
                    getClass().getName(), this.deleteOnClose, this.charset.name(), this.path
            );
        }


    }//JdbdTextPath

    private static final class JdbdBlobPath implements BlobPath {

        private final boolean deleteOnClose;

        private final Path path;

        private JdbdBlobPath(boolean deleteOnClose, Path path) {
            this.deleteOnClose = deleteOnClose;
            this.path = path;
        }

        @Override
        public boolean isDeleteOnClose() {
            return this.deleteOnClose;
        }

        @NonNull
        @Override
        public Path value() {
            return this.path;
        }

        @Override
        public String toString() {
            return String.format("%s[ deleteOnClose : %s , path : %s]",
                    getClass().getName(), this.deleteOnClose, this.path
            );
        }


    }//JdbdBlobPath

    static Point point(double x, double y) {
        return new JdbdPoint(x, y);
    }

    private static final class JdbdPoint implements Point {

        private final double x;

        private final double y;


        private JdbdPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public double getX() {
            return this.x;
        }

        @Override
        public double getY() {
            return this.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JdbdPoint) {
                final JdbdPoint o = (JdbdPoint) obj;
                match = Double.compare(o.x, this.x) == 0 && Double.compare(o.y, this.y) == 0;
            } else {
                match = false;
            }
            return match;
        }


        @Override
        public String toString() {
            return String.format("Point(%s %s)", this.x, this.y);
        }


    }//JdbdPoint


}
