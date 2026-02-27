package org.MemoryMeasure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Calculates the shallow memory size of a single object.
 * <p>
 * Counts only non-static fields declared in the class:
 * - Primitive fields are measured using their defined primitive sizes.
 * - Reference fields are counted as reference slots (assumed 8 bytes).
 * - Traverses the entire class hierarchy to include fields declared in all superclasses.
 */
public class PrimitivesObjectsMemory {

    /**
     * Estimates the shallow size of the given object instance.
     *
     * @param obj object to measure
     */
    static public long measure(Object obj) {
        if (obj == null) return 0;
        long sum = 0;

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                Class<?> type = field.getType();

                if (type.isPrimitive()) {
                    sum += PrimitivesSizes.of(type);
                } else {
                    throw new IllegalArgumentException("Reference Fields are not supported in PrimitivesObjectsMemory");
                }
            }
            clazz = clazz.getSuperclass();
        }
        return sum;
    }
}