/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.util;

import io.jdbd.lang.Nullable;
import io.jdbd.util.JdbdUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JdbdCollections {

    protected JdbdCollections() {
        throw new UnsupportedOperationException();
    }


    public static <T> List<T> unmodifiableList(List<T> list) {
        switch (list.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1:
                list = Collections.singletonList(list.get(0));
                break;
            default:
                list = Collections.unmodifiableList(list);
        }
        return list;

    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        switch (map.size()) {
            case 0:
                map = Collections.emptyMap();
                break;
            case 1: {
                for (Map.Entry<K, V> e : map.entrySet()) {
                    map = Collections.singletonMap(e.getKey(), e.getValue());
                    break;
                }
            }
            break;
            default:
                map = Collections.unmodifiableMap(map);
        }
        return map;
    }

    public static <T> Set<T> unmodifiableSet(Set<T> set) {
        switch (set.size()) {
            case 0:
                set = Collections.emptySet();
                break;
            case 1: {
                for (T t : set) {
                    set = Collections.singleton(t);
                    break;
                }
            }
            break;
            default:
                set = Collections.unmodifiableSet(set);
        }
        return set;
    }


    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.size() == 0;
    }


    /**
     * @return a modified map
     */
    public static Map<String, Object> loadProperties(final Path path) throws IOException {

        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            final Properties properties = new Properties();
            properties.load(in);
            final Map<String, Object> map = JdbdCollections.hashMap((int) (properties.size() / 0.75F));
            for (Object key : properties.keySet()) {
                String k = key.toString();
                map.put(k, properties.getProperty(k));
            }
            return map;
        }

    }


    public static <K, V> HashMap<K, V> hashMap() {
        return new FinalHashMap<>();
    }

    public static <K, V> HashMap<K, V> hashMap(int initialCapacity) {
        return new FinalHashMap<>(initialCapacity);
    }

    public static <K, V> HashMap<K, V> hashMap(Map<? extends K, ? extends V> m) {
        return new FinalHashMap<>(m);
    }

    public static <K, V> HashMap<K, V> hashMapIgnoreKey(Object ignoreKey) {
        return new FinalHashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return JdbdUtils.concurrentHashMap();
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int initialCapacity) {
        return JdbdUtils.concurrentHashMap(initialCapacity);
    }

    public static <E> ArrayList<E> arrayList() {
        return new FinalArrayList<>();
    }

    public static <E> ArrayList<E> arrayList(int initialCapacity) {
        return new FinalArrayList<>(initialCapacity);
    }

    public static <E> ArrayList<E> arrayList(Collection<? extends E> c) {
        return new FinalArrayList<>(c);
    }

    public static <E> LinkedList<E> linkedList() {
        return new FinalLinkedList<>();
    }

    public static <E> LinkedList<E> linkedList(Collection<? extends E> c) {
        return new FinalLinkedList<>(c);
    }

    public static <E> HashSet<E> hashSet() {
        return new FinalHashSet<>();
    }

    public static <E> HashSet<E> hashSet(int initialCapacity) {
        return new FinalHashSet<>(initialCapacity);
    }

    public static <E> HashSet<E> hashSet(Collection<? extends E> c) {
        return new FinalHashSet<>(c);
    }


    public static <T> List<T> safeUnmodifiableList(@Nullable List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return unmodifiableList(list);

    }

    public static <K, V> Map<K, V> safeUnmodifiableMap(@Nullable Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return unmodifiableMap(map);
    }


    public static <T> List<T> safeList(@Nullable List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> List<T> asUnmodifiableList(final Collection<T> collection) {
        final List<T> list;
        switch (collection.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1: {
                if (collection instanceof List) {
                    list = Collections.singletonList(((List<T>) collection).get(0));
                } else {
                    List<T> temp = null;
                    for (T v : collection) {
                        temp = Collections.singletonList(v);
                        break;
                    }
                    list = temp;
                }

            }
            break;
            default: {
                list = Collections.unmodifiableList(arrayList(collection));
            }

        }
        return list;

    }


    /**
     * prevent default deserialization
     */
    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize this");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize this");
    }


    private static final class FinalHashMap<K, V> extends HashMap<K, V> {

        private FinalHashMap() {
        }

        private FinalHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        private FinalHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

    }//FinalHashMap


    private static final class FinalArrayList<E> extends ArrayList<E> {

        private FinalArrayList() {
        }

        private FinalArrayList(int initialCapacity) {
            super(initialCapacity);
        }

        private FinalArrayList(Collection<? extends E> c) {
            super(c);
        }

    }//FinalArrayList


    private static final class FinalLinkedList<E> extends LinkedList<E> {

        private FinalLinkedList() {
        }

        private FinalLinkedList(Collection<? extends E> c) {
            super(c);
        }

    }//FinalLinkedList


    private static final class FinalHashSet<E> extends HashSet<E> {

        private FinalHashSet() {
        }

        private FinalHashSet(Collection<? extends E> c) {
            super(c);
        }

        private FinalHashSet(int initialCapacity) {
            super(initialCapacity);
        }


    }//FinalHashSet




}
