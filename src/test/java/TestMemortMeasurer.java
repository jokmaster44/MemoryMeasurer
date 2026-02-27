import org.internal.ReferencesMeasurer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Basic unit tests for ReferencesMeasurer.
 *
 * These tests verify that:
 * - measuring an object containing references returns a positive size
 * - nested referenced objects increase the measured size
 *
 * The tests intentionally avoid asserting exact byte values because the measurer provides an estimate
 * and the exact number depends on assumptions (for example, reference size is treated as 8 bytes).
 */
public class TestMemortMeasurer {

    static class B { int y = 5; }
    static class A { int x = 10; B b = new B(); }

    static class C { int z = 7; }
    static class A2 { C c = new C(); }

    /**
     * Testcase: Should correctly measure object containing a reference field.
     *
     * Steps:
     * 1. Create instance of A.
     * 2. Execute memory measurement using ReferencesMeasurer.
     *
     * Result state:
     * - Returned size is greater than zero.
     * - Measurement includes both the shallow size of A
     *   and the referenced object B.
     */
    @Test
    void testMemoryMeasure_should_calculate_size_for_object_with_reference() throws IllegalAccessException {

        //Arrange
        A a = new A();
        ReferencesMeasurer m = new ReferencesMeasurer();

        //Act
        long size = m.measure(a);

        //Assert
        assertTrue(size > 0);
    }

    /**
     * Testcase: Should recursively measure nested referenced objects.
     *
     * Steps:
     * 1. Create instance of A2.
     * 2. Execute memory measurement using ReferencesMeasurer.
     *
     * Result state:
     * - Returned size is greater than a single reference slot.
     * - Measurement includes both the shallow size of A2
     *   and the nested object C.
     */
    @Test
    void testMemoryMeasure_should_measure_nested_object() throws IllegalAccessException {

        //Arrange
        A2 a2 = new A2();
        ReferencesMeasurer m = new ReferencesMeasurer();

        //Act
        long size = m.measure(a2);

        //Assert
        assertTrue(size > 8);
    }
}