package org.example;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;





public final class MemoryMeasurer {
    private MemoryMeasurer() {
    }


    private final static long REF_SIZE = 8;

    public static long measure(Object root) {
        if (root == null) return 0;

        long total = 0;

        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Object> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Object obj = stack.pop();
            if (obj == null) continue;
            if (!visited.add(obj)) continue;

            Class<?> clazz = obj.getClass();

            // 1) Если obj — массив
            if (clazz.isArray()) {
                int len = Array.getLength(obj);
                Class<?> component = clazz.getComponentType();

                if (component.isPrimitive()) {
                    total += (long) len * primitiveSize(component);
                } else {
                    total += (long) len * REF_SIZE;

                    // тут мы обходим елементы масива как я понял
                    for (int i = 0; i < len; i++) {
                        Object element = Array.get(obj, i);
                        if (element != null && !shouldSkipDeepDive(element.getClass())) {
                            stack.push(element);
                        }
                    }
                }
                continue;
            }

            // 2) Обычный объект: поля + суперклассы
            Class<?> current = clazz;
            while (current != null) {
                Field[] fields = current.getDeclaredFields();

                for (Field f : fields) {
                    if (Modifier.isStatic(f.getModifiers())) continue;

                    Class<?> type = f.getType();

                    if (type.isPrimitive()) {
                        total += primitiveSize(type);
                    } else {
                        total += REF_SIZE;

                        try {
                            f.setAccessible(true);
                            Object value = f.get(obj);

                            // если value != null, идём глубже (но java.* лучше пропускать позже)
                            if( value != null && !shouldSkipDeepDive(value.getClass())){
                                stack.push(value);
                            }
                        } catch (Exception ignored) {
                            // не смогли прочитать поле — просто пропускаем deep dive
                        }
                    }
                }

                current = current.getSuperclass();
            }
        }

        return total;
    }


    private static int primitiveSize (Class <?> type) {
        if (type == int.class) {
            return 4;
        }else if (type == long.class){
            return 8;
        }else if (type == boolean.class){
            return 1;
        }else if (type == double.class){
            return 8;
        }else if (type == float.class){
            return 4;
        }else if (type == char.class){
            return 2;
        }else if ( type == short.class){
            return 2;
        }else if ( type == byte.class) {
            return 1;
        }else {
            throw new IllegalArgumentException("Unknown primitive: " + type);
        }
    }

    private static boolean shouldSkipDeepDive(Class<?> type) {
        String name = type.getName();
        return name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("jdk.")
                || name.startsWith("sun.");
    }




}

