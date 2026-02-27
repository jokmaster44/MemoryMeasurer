import org.internal.PrimitivesObjectsMemoryMeasure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestMemoryPrimitivesObjects {

    /**
     * Testcase: Should return zero when measuring a null object.
     * <p>
     * Steps:
     * 1. Create null object reference.
     * <p>
     * Result state:
     * - Returned size equals 0.
     * - No exception is thrown.
     */
    @Test
    void testMemory_nullObject_returnZero() {

        //Arrange
        PrimitivesObjectsMemoryMeasure measurer = new PrimitivesObjectsMemoryMeasure();
        Object obj = null;
        long expected = 0;

        //Act
        long actual = measurer.measure(obj);

        //Assert
        assertEquals(expected, actual, " Simple size of null should be 0");
    }

    /**
     * Testcase: Should return zero for an object without instance fields.
     * <p>
     * Steps:
     * 1. Create instance of empty class.
     * <p>
     * Result state:
     * - Returned size equals 0.
     * - No fields are counted.
     */
    @Test
    void testMemory_objecthasNoFields_returnsZero() {

        //Arrange
        class A {
        }
        PrimitivesObjectsMemoryMeasure measure = new PrimitivesObjectsMemoryMeasure();
        A a = new A();
        long expected = 0;

        //Act
        long actual = measure.measure(a);

        //Assert
        assertEquals(expected, actual, " Objects with no fields should have size 0");
    }

    /**
     * Testcase: Should correctly sum sizes of primitive and reference fields.
     * <p>
     * Steps:
     * 1. Create instance containing primitive and reference fields.
     * <p>
     * Result state:
     * - Primitive fields are summed according to their defined sizes.
     * - Reference fields are counted as reference slots.
     */
    @Test
    void testMemory_objectWithPrimitives_countPrimitiveSizes() {

        //Arrange
        class Primitive {
            int a;
            int b;
            long c;
            String g;
            char d;
        }

        PrimitivesObjectsMemoryMeasure measurer = new PrimitivesObjectsMemoryMeasure();
        Primitive a = new Primitive();
        long expected = 26L;

        //Act
        long actual = measurer.measure(a);

        //Assert
        assertEquals(expected, actual, " Should sum primitive field sizes using PrimitivesSizes");
    }

    /**
     * Testcase: Should count reference fields as fixed-size reference slots.
     * <p>
     * Steps:
     * 1. Create instance containing reference fields.
     * <p>
     * Result state:
     * - Each reference field contributes one reference slot.
     * - No deep traversal is performed in shallow measurement.
     */
    @Test
    void measure_objectWithReferences_countsReferenceSlots() {

        // Arrange
        class RefHolder {
            Object o;
            String s;
        }

        PrimitivesObjectsMemoryMeasure measurer = new PrimitivesObjectsMemoryMeasure();
        long expected = 8L + 8L;

        // Act
        long actual = measurer.measure(new RefHolder());

        // Assert
        assertEquals(expected, actual,
                "Each reference field should count as one slot (8 bytes)");
    }

    /**
     * Testcase: Should ignore static fields during measurement.
     * <p>
     * Steps:
     * 1. Create class with instance and static fields.
     * <p>
     * Result state:
     * - Static fields are ignored.
     * - Only non-static instance fields are counted.
     */
    @Test
    void measure_ignoresStaticFields() {

        // Arrange
        class WithStatic {
            int a;
            static long s;
        }

        PrimitivesObjectsMemoryMeasure measurer = new PrimitivesObjectsMemoryMeasure();
        long expected = 4L;

        // Act
        long actual = measurer.measure(new WithStatic());

        // Assert
        assertEquals(expected, actual,
                "Static fields must be ignored");
    }

    /**
     * Testcase: Should include non-static fields declared in superclasses.
     * <p>
     * Steps:
     * 1. Create subclass that extends a parent class with fields.
     * 2. Execute memory measurement on subclass instance.
     * <p>
     * Result state:
     * - Fields declared in both subclass and superclass are counted.
     */
    @Test
    void measure_includesSuperclassFields() {

        // Arrange
        class Parent {
            int p;
        }

        class Child extends Parent {
            long c;
        }

        PrimitivesObjectsMemoryMeasure measurer = new PrimitivesObjectsMemoryMeasure();
        long expected = 4 + 8;

        // Act
        long actual = measurer.measure(new Child());

        // Assert
        assertEquals(expected, actual,
                "Should include non-static fields declared in superclasses");
    }
}