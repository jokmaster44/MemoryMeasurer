package org.MemoryMeasure;

/**
 * Public entry point for memory measurement.
 * <p>
 * Delegates the actual traversal and size calculation to ReferencesMeasurer}.
 * Provides a convenient static API: {@code MemoryMeasurer.measure(obj)}.
 */
public final class MemoryMeasurer {

    private MemoryMeasurer() {
    }

    /**
     * Measures the memory footprint of the given object graph.
     *
     * @param obj root object to measure (may be null)
     * @return estimated size in bytes based on current measurer rules
     * @throws IllegalAccessException if reflective access fails
     */
    public static long measure(Object obj) throws IllegalAccessException {
        return new ReferencesMemory().measure(obj);
    }
}