package org.internal;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;


/**
 * Utility responsible for estimating the memory usage of array objects.
 *
 * For primitive arrays, size is calculated based on element type and length.
 * For reference arrays, reference slots are counted and elements are
 * measured recursively.
 */
public class ArrayMeasurer {

    /**
     * Estimates the memory contribution of the given array.
     *
     * If the array contains primitives, the result is based on primitive element size multiplied by array length.
     *
     * If the array contains object references, each reference slot
     * is counted and non-null elements are measured recursively.
     *
     * @param visited identity-based map used to prevent double counting
     * @param recursion measurer used for recursive object traversal
     */
    public long measureArray(Object array,
                             IdentityHashMap<Object, Boolean> visited,
                             ReferencesMeasurer recursion) throws IllegalAccessException {

        int length = Array.getLength(array);
        Class<?> componentType = array.getClass().getComponentType();

        if (componentType.isPrimitive()) {
            return (long) length * PrimitivesSizes.of(componentType);
        }

        long sum = (long) length * 8;
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            if (element != null) {
                sum += recursion.measureInternal(element, visited);
            }
        }
        return sum;
    }
}