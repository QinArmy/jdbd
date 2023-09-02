package io.jdbd.util;

import io.jdbd.lang.Nullable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JdbdUtils {

    private JdbdUtils() {
        throw new UnsupportedOperationException();
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

    public static IllegalArgumentException requiredText(String paramName) {
        return new IllegalArgumentException(String.format("%s must have text", paramName));
    }


    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return new FinalConcurrentHashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int capacity) {
        return new FinalConcurrentHashMap<>(capacity);
    }


    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }


    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize JdbdUtils");
    }


    private static final class FinalConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private FinalConcurrentHashMap() {
        }

        private FinalConcurrentHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }//FinalConcurrentHashMap
}
