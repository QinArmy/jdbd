package io.jdbd.vendor.util;


import io.jdbd.lang.NonNull;
import io.jdbd.lang.Nullable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class JdbdArrays {

    protected JdbdArrays() {
        throw new UnsupportedOperationException();
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> Set<E> asSet(@Nullable E... elements) {
        final Set<E> set;
        if (elements == null || elements.length == 0) {
            set = Collections.emptySet();
        } else if (elements.length == 1) {
            set = Collections.singleton(elements[0]);
        } else {
            set = new HashSet<>((int) (elements.length / 0.75F));
            Collections.addAll(set, elements);
        }
        return set;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> unmodifiableListOf(T first, T... rest) {
        final List<T> list;
        if (rest.length == 0) {
            list = Collections.singletonList(first);
        } else {
            final List<T> temp = JdbdCollections.arrayList(1 + rest.length);
            temp.add(first);
            Collections.addAll(temp, rest);
            list = Collections.unmodifiableList(temp);
        }
        return list;
    }


    public static Object createArrayInstance(final Class<?> componentClass, final int dimension, final int length) {
        if (componentClass.isArray()) {
            throw new IllegalArgumentException("targetArrayClass error.");
        }
        final Class<?> componentType;
        if (dimension == 1) {
            componentType = componentClass;
        } else if (dimension > 1) {
            try {
                componentType = Class.forName(obtainComponentClassName(componentClass, dimension));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalArgumentException(String.format("dimension[%s] less than 1", dimension));
        }
        return Array.newInstance(componentType, length);
    }

    private static String obtainComponentClassName(final Class<?> targetArrayClass, final int dimension) {
        final String className;
        if (targetArrayClass.isPrimitive()) {
            if (targetArrayClass == int.class) {
                className = "I";
            } else if (targetArrayClass == long.class) {
                className = "J";
            } else if (targetArrayClass == short.class) {
                className = "S";
            } else if (targetArrayClass == byte.class) {
                className = "B";
            } else if (targetArrayClass == boolean.class) {
                className = "Z";
            } else if (targetArrayClass == float.class) {
                className = "F";
            } else if (targetArrayClass == double.class) {
                className = "D";
            } else if (targetArrayClass == char.class) {
                className = "C";
            } else {
                String m = String.format("targetArrayClass[%s] not supported", targetArrayClass.getName());
                throw new IllegalArgumentException(m);
            }
        } else {
            className = targetArrayClass.getName();
        }


        final StringBuilder builder = new StringBuilder(dimension + 2 + className.length());
        // i = 1 not i = 0, because Array.newInstance need componentType
        for (int i = 1; i < dimension; i++) {
            builder.append('[');
        }
        final boolean appendL = !targetArrayClass.isPrimitive();
        if (appendL) {
            builder.append('L');
        }
        builder.append(className);
        if (appendL) {
            builder.append(';');
        }
        return builder.toString();
    }


    public static Class<?> underlyingComponent(final Class<?> arrayType) {
        if (!arrayType.isArray()) {
            throw new IllegalArgumentException(String.format("%s isn't array type", arrayType.getName()));
        }

        Class<?> componentType;
        componentType = arrayType.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        return componentType;
    }

    public static int dimensionOf(final Class<?> arrayType) {
        assert arrayType.isArray();
        int dimension = 1;
        Class<?> componentType;
        componentType = arrayType.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            dimension++;
        }
        return dimension;
    }

    public static Class<?> arrayClassOf(final Class<?> elementType, final int dimension) {
        if (dimension < 1) {
            throw new IllegalArgumentException("dimension error");
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            builder.append('[');
        }
        if (elementType.isArray()) {
            builder.append(elementType.getName());
        } else if (elementType.isAnonymousClass()) {
            throw new IllegalArgumentException("don't support anonymous class");
        } else if (!(elementType.isPrimitive())) {
            builder.append('L')
                    .append(elementType.getName())
                    .append(';');
        } else if (elementType == int.class) {
            builder.append('I');
        } else if (elementType == long.class) {
            builder.append('J');
        } else if (elementType == boolean.class) {
            builder.append('Z');
        } else if (elementType == byte.class) {
            builder.append('B');
        } else if (elementType == char.class) {
            builder.append('C');
        } else if (elementType == double.class) {
            builder.append('D');
        } else if (elementType == float.class) {
            builder.append('F');
        } else if (elementType == short.class) {
            builder.append('S');
        } else {
            String m = String.format("don't support %s", elementType.getName());
            throw new IllegalArgumentException(m);
        }
        try {
            return Class.forName(builder.toString());
        } catch (ClassNotFoundException e) {
            //no bug, never here
            throw new RuntimeException(e);
        }
    }

    public static Class<?> arrayClassOf(final Class<?> elementType) {
        return arrayClassOf(elementType, 1);
    }


}
