import org.MemoryMeasure.MemoryMeasurer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestMemoryArray {

    /**
     * Testcase: Should correctly measure primitive array size.
     * <p>
     * Steps:
     * 1. Create primitive int array with defined length.
     * 2. Execute memory measurement.
     * <p>
     * Result state:
     * - Returned size equals array length multiplied by primitive size.
     */
    @Test
    void testPrimitiveArray_countsLengthTimes() throws Exception {

        //Arrange
        int[] arr = new int[5];
        long expected = 5L * 4;

        //Act
        long actual = MemoryMeasurer.measure(arr);

        //Assert
        assertEquals(expected, actual,
                "Primitive int array size should be length * 4 bytes");
    }

    /**
     * Testcase: Should measure reference array including deep object sizes.
     * <p>
     * Steps:
     * 1. Create array containing object references and null element.
     * <p>
     * Result state:
     * - Each array slot contributes one reference size.
     * - Non-null elements are measured recursively.
     * - Null elements do not contribute deep size.
     */
    @Test
    void testArray_countsSlotsAndDeep() throws Exception {

        //Arrange
        class A {
            long x;
        }

        A[] arr = new A[]{new A(), null, new A()};
        long expected = 3L * 8 + 2L * 8;

        //Act
        long actual = MemoryMeasurer.measure(arr);

        //Assert
        assertEquals(expected, actual,
                "Reference array should count slots and deep object sizes correctly");
    }

    /**
     * Testcase: Should not double-count the same object referenced multiple times.
     * <p>
     * Steps:
     * 1. Create array where the same object instance appears multiple times.
     * <p>
     * Result state:
     * - Each array slot contributes reference size.
     * - Shared object instance is measured only once.
     * - No double-counting occurs due to visited tracking.
     */
    @Test
    void testArrayWithSameObject_notDoubleCounted() throws Exception {

        //Arrange
        class A {
            long x;
        }

        A obj = new A();
        A[] arr = new A[]{obj, obj};
        long expected = 2 * 8 + 8L;

        //Act
        long actual = MemoryMeasurer.measure(arr);

        //Assert
        assertEquals(expected, actual,
                "Same object referenced twice should not be double-counted");
    }
}