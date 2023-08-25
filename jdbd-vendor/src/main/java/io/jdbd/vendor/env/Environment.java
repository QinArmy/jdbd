package io.jdbd.vendor.env;

import io.jdbd.JdbdException;
import io.jdbd.lang.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public interface Environment {

    /**
     * @return a unmodified map
     */
    Map<String, Object> sourceMap();

    @Nullable
    <T> T get(Key<T> key) throws JdbdException;

    <T> T get(Key<T> key, Supplier<T> supplier) throws JdbdException;

    <T> T getOrDefault(Key<T> key) throws JdbdException;


    <T extends Comparable<T>> T getInRange(Key<T> key, T minValue, T maxValue) throws JdbdException;

    <T> T getRequired(Key<T> key) throws JdbdException;

    /**
     * @throws JdbdException throw when key default is null
     */
    boolean isOn(Key<Boolean> key) throws JdbdException;

    /**
     * @throws JdbdException throw when key default is null
     */
    boolean isOff(Key<Boolean> key) throws JdbdException;


}
