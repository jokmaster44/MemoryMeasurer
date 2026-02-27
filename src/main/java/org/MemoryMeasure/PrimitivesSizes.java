package org.MemoryMeasure;

public final class PrimitivesSizes {

    static int of(Class<?> type) {
        if (type == int.class) return 4;
        if (type == long.class) return 8;
        if (type == double.class) return 8;
        if (type == char.class) return 2;
        if (type == short.class) return 2;
        if (type == float.class) return 4;
        if (type == byte.class) return 1;
        if (type == boolean.class) return 1;

        throw new IllegalArgumentException("Not a primitive" + type);
    }
}
