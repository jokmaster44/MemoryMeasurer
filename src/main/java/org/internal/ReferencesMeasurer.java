package org.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;


/**
 * Entry point for measuring the memory footprint of an object graph.
 *
 * Calculates the shallow size of an object and recursively measures
 * all non-static reference fields. Arrays are delegated to ArrayMeasurer.
 *
 * Uses IdentityHashMap to avoid double-counting and infinite recursion
 * in cyclic object graphs.
 */
public class ReferencesMeasurer {

    private final SimpleObjectsMeasurer simple = new SimpleObjectsMeasurer();
    private final ArrayMeasurer arrayMeasurer = new ArrayMeasurer();

    /**
     * Measures the given object and all objects reachable from it.
     *
     * @param obj root object to measure
     */
    public long measure(Object obj) throws IllegalAccessException {
        return measureInternal(obj, new IdentityHashMap<>());
    }

    /**
     * Internal recursive measurement method.
     * Shared visited map ensures each object is counted only once.
     */
    long measureInternal(Object obj, IdentityHashMap<Object, Boolean> visited) throws IllegalAccessException {
        if (obj == null) return 0L;

        if (visited.containsKey(obj)) return 0L;
        visited.put(obj, true);

        if (obj.getClass().isArray()) {
            return arrayMeasurer.measureArray(obj, visited, this);
        }

        long sum = simple.measure(obj);

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);

            if (field.getType().isPrimitive()) continue;

            Object value = field.get(obj);
            if (value != null) {
                sum += measureInternal(value, visited);
            }
        }
        return sum;
    }
}