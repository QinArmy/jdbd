package io.jdbd.vendor.env;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdNumbers;
import reactor.netty.resources.ConnectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Converters {

    private Converters() {
        throw new UnsupportedOperationException();
    }

    public static final Map<Class<?>, BiFunction<Class<?>, String, ?>> CONVERTOR_MAP;


    static {
        final Map<Class<?>, BiFunction<Class<?>, String, ?>> map = JdbdCollections.hashMap();
        registerDefaultConverter(map::put);
        CONVERTOR_MAP = JdbdCollections.unmodifiableMap(map);
    }

    public static final BiFunction<Class<?>, String, ?> NAME_ENUM_CONVERTOR = Converters::toEnum;

    public static final BiFunction<Class<?>, String, ?> TEXT_ENUM_CONVERTOR = Converters::toTextEnum;

    public static void registerDefaultConverter(final BiConsumer<Class<?>, BiFunction<Class<?>, String, ?>> consumer) {

        consumer.accept(String.class, Converters::stringToString);
        consumer.accept(Byte.class, Converters::toByte);
        consumer.accept(Short.class, Converters::toShort);
        consumer.accept(Integer.class, Converters::toInt);

        consumer.accept(Long.class, Converters::toLong);
        consumer.accept(Float.class, Converters::toFloat);
        consumer.accept(Double.class, Converters::toDouble);
        consumer.accept(BigDecimal.class, Converters::toBigDecimal);

        consumer.accept(BigInteger.class, Converters::toBigInteger);
        consumer.accept(Boolean.class, Converters::toBoolean);
        consumer.accept(Path.class, Converters::toPath);
        consumer.accept(Charset.class, Converters::toCharset);

        consumer.accept(UUID.class, Converters::toUUID);
        consumer.accept(ZoneOffset.class, Converters::toZoneOffset);

        consumer.accept(ConnectionProvider.class, Converters::createInstanceFromSupplier);

    }


    /**
     * @throws JdbdException throw when not found match convertor.
     */
    @SuppressWarnings("unchecked")
    public static <T> BiFunction<Class<T>, String, T> findConvertor(final Class<T> targetClass) throws JdbdException {

        final BiFunction<?, String, ?> convertor;

        if (!Enum.class.isAssignableFrom(targetClass)) {
            convertor = CONVERTOR_MAP.get(targetClass);
        } else if (TextEnum.class.isAssignableFrom(targetClass)) {
            convertor = TEXT_ENUM_CONVERTOR;
        } else {
            convertor = NAME_ENUM_CONVERTOR;
        }
        if (convertor == null) {
            String m = String.format("Not found convertor for %s", targetClass.getName());
            throw new JdbdException(m);
        }
        return (BiFunction<Class<T>, String, T>) convertor;
    }

    public static Object createInstanceFromSupplier(final Class<?> interfaceClass, final String supplierRef) {

        try {
            final int colonIndex;
            colonIndex = supplierRef.indexOf("::");

            final Class<?> implClass;
            implClass = Class.forName(supplierRef.substring(0, colonIndex));

            final String methodName;
            methodName = supplierRef.substring(colonIndex + 2);

            final Object instance;
            if ("NEW".equals(methodName)) {
                instance = implClass.getConstructor().newInstance();
            } else {
                instance = invokeSupplierMethod(interfaceClass, implClass.getMethod(methodName), supplierRef);
            }
            return instance;
        } catch (IndexOutOfBoundsException e) {
            String m = String.format("supplier function %s error", supplierRef);
            throw new JdbdException(m, e);
        } catch (JdbdException e) {
            throw e;
        } catch (Throwable e) {
            throw convertFailure(supplierRef, interfaceClass, e);
        }

    }

    /**
     * @see #createInstanceFromSupplier(Class, String)
     */
    private static Object invokeSupplierMethod(final Class<?> interfaceClass, final Method method,
                                               final String supplierRef)
            throws InvocationTargetException, IllegalAccessException {

        final int modifier;
        modifier = method.getModifiers();

        final boolean match;
        match = Modifier.isPublic(modifier)
                && Modifier.isStatic(modifier)
                && method.getParameterCount() == 0
                && interfaceClass.isAssignableFrom(method.getReturnType());

        if (!match) {
            String m = String.format("%s not public static factory supplier method.", supplierRef);
            throw new JdbdException(m);
        }
        return method.invoke(null);
    }


    private static String stringToString(final Class<?> targetType, final String source) {
        return source;
    }


    private static Byte toByte(final Class<?> targetType, final String source) throws JdbdException {
        try {
            final byte value;
            if (JdbdNumbers.isHexNumber(source)) {
                value = Byte.decode(source);
            } else {
                value = Byte.parseByte(source);
            }
            return value;
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static Short toShort(final Class<?> targetType, final String source) throws JdbdException {
        try {
            final short value;
            if (JdbdNumbers.isHexNumber(source)) {
                value = Short.decode(source);
            } else {
                value = Short.parseShort(source);
            }
            return value;
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }


    private static Integer toInt(final Class<?> targetType, final String source) throws JdbdException {
        try {
            final int value;
            if (JdbdNumbers.isHexNumber(source)) {
                value = Integer.decode(source);
            } else {
                value = Integer.parseInt(source);
            }
            return value;
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static Long toLong(final Class<?> targetType, final String source) throws JdbdException {
        try {
            final long value;
            if (JdbdNumbers.isHexNumber(source)) {
                value = Long.decode(source);
            } else {
                value = Long.parseLong(source);
            }
            return value;
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static Float toFloat(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return Float.parseFloat(source);
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static Double toDouble(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return Double.parseDouble(source);
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static BigDecimal toBigDecimal(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return new BigDecimal(source);
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }


    private static BigInteger toBigInteger(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return new BigInteger(source);
        } catch (NumberFormatException e) {
            throw convertFailure(source, targetType, e);
        }
    }


    private static Boolean toBoolean(final Class<?> targetType, final String source) throws JdbdException {
        final boolean value;
        switch (source.toLowerCase(Locale.ROOT)) {
            case "true":
            case "on":
            case "yes":
            case "1":
                value = true;
                break;
            case "false":
            case "off":
            case "no":
            case "0":
                value = false;
                break;
            default:
                throw convertFailure(source, targetType, null);
        }
        return value;
    }

    private static Path toPath(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return Paths.get(source);
        } catch (Exception e) {
            throw convertFailure(source, targetType, e);
        }
    }


    private static Charset toCharset(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return Charset.forName(source);
        } catch (Exception e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static UUID toUUID(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return UUID.fromString(source);
        } catch (Exception e) {
            throw convertFailure(source, targetType, e);
        }
    }

    private static ZoneOffset toZoneOffset(final Class<?> targetType, final String source) throws JdbdException {
        try {
            return ZoneOffset.of(source);
        } catch (Exception e) {
            throw convertFailure(source, targetType, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T toEnum(final Class<?> targetType, final String source) throws JdbdException {

        try {
            return Enum.valueOf((Class<T>) getEnumClass(targetType), source);
        } catch (IllegalArgumentException e) {
            throw convertFailure(source, targetType, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & TextEnum> T toTextEnum(final Class<?> targetType, final String source)
            throws JdbdException {
        if (!TextEnum.class.isAssignableFrom(targetType)) {
            String m = String.format("%s isn't %S.", targetType.getName(), TextEnum.class.getName());
            throw new JdbdException(m);
        }
        final T value;
        value = (T) TextEnumHelper.getTextMap(getEnumClass(targetType)).get(source);
        if (value == null) {
            throw convertFailure(source, targetType, null);
        }
        return value;
    }

    private static Class<?> getEnumClass(Class<?> targetType) throws JdbdException {
        if (!Enum.class.isAssignableFrom(targetType)) {
            String m = String.format("%s isn't enum class.", targetType.getName());
            throw new JdbdException(m);
        }
        if (targetType.isAnonymousClass()) {
            targetType = targetType.getSuperclass();
        }
        return targetType;
    }


    private static class TextEnumHelper {

        private static final ConcurrentMap<Class<?>, Map<String, ? extends TextEnum>> INSTANCE_MAP =
                JdbdCollections.concurrentHashMap();

        private static final Function<Class<?>, Map<String, ? extends TextEnum>> FUNCTION = TextEnumHelper::createEnumMap;


        @SuppressWarnings("unchecked")
        private static <T extends TextEnum> Map<String, T> getTextMap(Class<?> enumClass) {
            return (Map<String, T>) INSTANCE_MAP.computeIfAbsent(enumClass, FUNCTION);
        }

        @SuppressWarnings("unchecked")
        private static <T extends TextEnum> Map<String, T> createEnumMap(final Class<?> enumClass) {
            if (!Enum.class.isAssignableFrom(enumClass) || TextEnum.class.isAssignableFrom(enumClass)) {
                throw new JdbdException(String.format("%s isn't %s.", enumClass.getName(), TextEnum.class));
            }
            for (Field field : enumClass.getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    throw new JdbdException(String.format("%s %s isn't final.", enumClass.getName(), field.getName()));
                }
            }
            final Class<T> clazz = (Class<T>) enumClass;
            final T[] array;
            array = clazz.getEnumConstants();
            final Map<String, T> map = JdbdCollections.hashMap((int) (array.length / 0.75f));

            String text;
            for (T e : array) {
                text = e.text();
                if (map.putIfAbsent(e.text(), e) != null) {
                    throw new JdbdException(String.format("%s text[%s] duplication.", enumClass.getName(), text));
                }
            }
            return JdbdCollections.unmodifiableMap(map);
        }

    }//TextEnumHelper


    private static JdbdException convertFailure(@Nullable String source, Class<?> javaType, @Nullable Throwable clause) {
        final String m = String.format("%s couldn't convert to %s .", source, javaType.getName());
        final JdbdException e;
        if (clause == null) {
            e = new JdbdException(m);
        } else {
            e = new JdbdException(m, clause);
        }
        return e;
    }

}
