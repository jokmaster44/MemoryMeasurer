import org.MemoryMeasure.PrimitivesObjectsMemory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
        PrimitivesObjectsMemory measurer = new PrimitivesObjectsMemory();
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
        PrimitivesObjectsMemory measure = new PrimitivesObjectsMemory();
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
            String g; // reference -> should cause exception
            char d;
        }

        //Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> PrimitivesObjectsMemory.measure(new Primitive()),
                "PrimitivesObjectsMemory should throw when object contains reference fields");
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
    void testMeasure_objectWithReferences_countsReferenceSlots() {

        //Arrange
        class RefHolder {
            Object o;
            String s;
        }

        //Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> PrimitivesObjectsMemory.measure(new RefHolder()),
                "PrimitivesObjectsMemory should throw when object contains reference fields");
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
    void testMeasure_ignoresStaticFields() {

        // Arrange
        class WithStatic {
            int a;
            static long s;
        }

        PrimitivesObjectsMemory measurer = new PrimitivesObjectsMemory();
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
    void testMeasure_includesSuperclassFields() {

        // Arrange
        class Parent {
            int p;
        }

        class Child extends Parent {
            long c;
        }

        PrimitivesObjectsMemory measurer = new PrimitivesObjectsMemory();
        long expected = 4 + 8;

        // Act
        long actual = measurer.measure(new Child());

        // Assert
        assertEquals(expected, actual,
                "Should include non-static fields declared in superclasses");
    }
}