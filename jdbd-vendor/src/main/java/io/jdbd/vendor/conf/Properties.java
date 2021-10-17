package io.jdbd.vendor.conf;

import io.jdbd.config.PropertyException;
import reactor.util.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Properties {

    /**
     * @return actual property pair count( not contain {@link System#getProperties()} and {@link System#getenv()})
     */
    int size();

    /**
     * @return a unmodifiable map
     */
    Map<String, String> getSource();

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key the property name to resolve
     */
    @Nullable
    String get(PropertyKey key);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key the property name to resolve
     */
    String get(PropertyKey key, String defaultValue);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     *
     * @param key        the property name to resolve
     * @param targetType the expected type of the property value
     */
    @Nullable
    <T> T get(PropertyKey key, Class<T> targetType) throws PropertyException;

    /**
     * Return the property value associated with the given key,
     * or {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param targetType   the expected type of the property value
     * @param defaultValue the default value to return if no value is found
     */
    <T> T get(PropertyKey key, Class<T> targetType, T defaultValue) throws PropertyException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key the property name to resolve
     * @return a  list
     */
    List<String> getList(PropertyKey key) throws PropertyException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty list the key cannot be resolved.
     *
     * @param key         the property name to resolve
     * @param elementType the expected type of the property value
     * @return a  list
     */
    <T> List<T> getList(PropertyKey key, Class<T> elementType) throws PropertyException;

    /**
     * Return the property value associated with the given key,
     * or {@link Collections#emptyList()} if the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param elementType the expected type of the property value
     * @param defaultList     the default list to return if no value is found
     * @return a  li
     */
    <T> List<T> getList(PropertyKey key, Class<T> elementType, List<T> defaultList) throws PropertyException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param elementType the expected type of the property value
     * @return a  list
     */
    <T> Set<T> getSet(PropertyKey key, Class<T> elementType) throws PropertyException;

    Set<String> getSet(PropertyKey key) throws PropertyException;

    /**
     * Return the property value associated with the given key,but not {@link String} ,the the property value showSQL:
     * {@code value1,value2,...,valuen}
     * or empty set the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param elementType the expected type of the property value
     * @param defaultSet      the default set to return if no value is found
     * @return a  list
     */
    <T> Set<T> getSet(PropertyKey key, Class<T> elementType, Set<T> defaultSet) throws PropertyException;

    /**
     * Return the property value associated with the given key (never {@code null}).
     *
     * @throws PropertyException if the key cannot be resolved
     */
    String getNonNull(PropertyKey key) throws PropertyException;

    /**
     * Return the property value associated with the given key, converted to the given
     * targetType (never {@code null}).
     *
     * @throws PropertyException if the given key cannot be resolved
     */
    <T> T getNonNull(PropertyKey key, Class<T> targetType) throws PropertyException;

    String getOrDefault(PropertyKey key) throws PropertyException;

    <T> T getOrDefault(PropertyKey key, Class<T> targetType) throws PropertyException;


}
