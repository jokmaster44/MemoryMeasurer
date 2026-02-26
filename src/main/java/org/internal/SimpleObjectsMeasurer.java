package org.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Calculates the shallow memory size of a single object.
 *
 * Counts only non-static fields declared in the class:
 * - Primitive fields are measured using their defined primitive sizes.
 * - Reference fields are counted as reference slots (assumed 8 bytes).
 */
public class SimpleObjectsMeasurer {

    /**
     * Estimates the shallow size of the given object instance.
     *
     * @param obj object to measure
     */
    public long measure(Object obj) {
        if(obj == null) return 0;
        long sum = 0;

        Field[] fields = obj.getClass().getDeclaredFields();

        for(Field field : fields){
            if(Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);
            Class<?> type = field.getType();

            if(type.isPrimitive()){
                sum += PrimitivesSizes.of(type);
            }else {
                sum += 8;
            }
        }
        return sum;
    }
}