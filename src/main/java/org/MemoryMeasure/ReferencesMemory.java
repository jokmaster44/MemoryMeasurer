package org.MemoryMeasure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

/**
 * Measures deep size of an object graph.
 * - Primitive fields: counted by primitive size
 * - Reference fields: counted as 8-byte slots + recursively measured objects
 * Arrays are delegated to ArrayMemory.
 */
public class ReferencesMemory {

    private final ArrayMemory arrayMeasurer = new ArrayMemory();

    public static long measure(Object obj) throws IllegalAccessException {
        return new ReferencesMemory().measureInternal(obj, new IdentityHashMap<>());
    }

    long measureInternal(Object obj, IdentityHashMap<Object, Boolean> visited) throws IllegalAccessException {
        if (obj == null) return 0L;

        if (visited.containsKey(obj)) return 0L;
        visited.put(obj, true);

        if (obj.getClass().isArray()) {
            return arrayMeasurer.measureArray(obj, visited, this);
        }

        long sum = 0L;

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Class<?> type = field.getType();

                if (type.isPrimitive()) {
                    sum += PrimitivesSizes.of(type);
                    continue;
                }

                // reference slot size
                sum += 8;

                Object value = field.get(obj);
                if (value != null) {
                    sum += measureInternal(value, visited);
                }
            }

            clazz = clazz.getSuperclass();
        }

        return sum;
    }
}