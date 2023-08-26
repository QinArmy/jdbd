package io.jdbd.vendor.env;

import io.jdbd.lang.Nullable;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdStrings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * @see Environment
 */
public abstract class Key<T> {


    public final String name;

    public final Class<T> valueClass;

    public final T defaultValue;

    protected Key(String name, Class<T> valueClass, @Nullable T defaultValue) {
        this.name = name;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
    }

    @Override
    public final String toString() {
        return JdbdStrings.builder()
                .append(getClass().getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" , valueClass : ")
                .append(this.valueClass.getName())
                .append(" , defaultValue : ")
                .append(this.defaultValue)
                .append(" ]")
                .toString();
    }


    @SuppressWarnings("unchecked")
    protected static <T extends Key<?>> List<T> createKeyList(final Class<?> keyClass,
                                                              final IntFunction<List<Key<?>>> constructor) {

        try {
            final Field[] fieldArray;
            fieldArray = keyClass.getDeclaredFields();

            final Map<String, Boolean> map = JdbdCollections.hashMap((int) (fieldArray.length / 0.75f));
            final List<Key<?>> list = constructor.apply(fieldArray.length);
            Key<?> key;
            int modifier;
            for (Field field : fieldArray) {
                modifier = field.getModifiers();
                if (keyClass.isAssignableFrom(field.getType())
                        && Modifier.isPublic(modifier)
                        && Modifier.isStatic(modifier)
                        && Modifier.isFinal(modifier)) {

                    key = (Key<?>) field.get(null);
                    if (map.putIfAbsent(key.name, Boolean.TRUE) != null) {
                        throw new IllegalStateException(String.format("%s duplication", key));
                    }
                    list.add(key);
                }

            }
            return (List<T>) Collections.unmodifiableList(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
