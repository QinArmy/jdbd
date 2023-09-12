package io.jdbd.util;

import io.jdbd.lang.Nullable;
import io.jdbd.type.PathParameter;
import io.jdbd.type.TextPath;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JdbdUtils {

    private JdbdUtils() {
        throw new UnsupportedOperationException();
    }


    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }


    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }


    public static boolean hasNoText(final @Nullable String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        boolean match = true;
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                continue;
            }
            match = false;
            break;
        }
        return match;
    }

    /**
     * @return a unmodified set.
     */
    public static Set<OpenOption> openOptionSet(final PathParameter parameter) {
        final Set<OpenOption> optionSet;
        if (parameter.isDeleteOnClose()) {
            final Set<OpenOption> temp = new HashSet<>(4);
            temp.add(StandardOpenOption.READ);
            temp.add(StandardOpenOption.DELETE_ON_CLOSE);
            optionSet = Collections.unmodifiableSet(temp);
        } else {
            optionSet = Collections.singleton(StandardOpenOption.READ);
        }
        return optionSet;
    }


    public static BufferedReader newBufferedReader(final TextPath textPath, final int bufferSize) throws IOException {
        final SeekableByteChannel channel;
        channel = Files.newByteChannel(textPath.value(), openOptionSet(textPath));

        final InputStream inputStream;
        inputStream = Channels.newInputStream(channel);

        final InputStreamReader inReader;
        inReader = new InputStreamReader(inputStream, textPath.charset().newDecoder());

        return new BufferedReader(inReader, bufferSize);
    }


    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return new FinalConcurrentHashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int capacity) {
        return new FinalConcurrentHashMap<>(capacity);
    }


    public static IllegalArgumentException requiredText(String paramName) {
        return new IllegalArgumentException(String.format("%s must have text", paramName));
    }


    private static final class FinalConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private FinalConcurrentHashMap() {
        }

        private FinalConcurrentHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }//FinalConcurrentHashMap
}
