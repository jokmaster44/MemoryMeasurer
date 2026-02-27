import org.MemoryMeasure.MemoryMeasurer;
import org.MemoryMeasure.ReferencesMemory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestMemoryReferences {

    /**
     * Testcase: Should return zero when measuring null reference.
     * <p>
     * Steps:
     * 1. Create null object reference.
     * <p>
     * Result state:
     * - Returned size equals 0.
     * - No exception is thrown.
     */
    @Test
    void testMemory_nullObject_returnZero() throws Exception {

        //Arrange
        Object obj = null;
        long expected = 0;

        //Act
        long actual = MemoryMeasurer.measure(obj);

        //Assert
        assertEquals(expected, actual,
                "Measuring null should return 0");
    }

    /**
     * Testcase: Should recursively measure nested referenced objects.
     * <p>
     * Steps:
     * 1. Create Parent instance containing reference to Child.
     * <p>
     * Result state:
     * - Shallow size of Parent is counted (reference slot).
     * - Shallow size of Child is included through recursive traversal.
     */
    @Test
    void testMemory_objectsWithChild_countsSimpleandDeep() throws Exception {

        //Arrange
        class Child {
            long x;
        }

        class Parent {
            Child child;
        }

        Parent obj = new Parent();
        obj.child = new Child();
        long expected = 8 + 8;

        //Act
        long actual = MemoryMeasurer.measure(obj);

        //Assert
        assertEquals(expected, actual,
                "Should count Parent shallow (reference slot) + Child shallow");
    }

    /**
     * Testcase: Should return zero for object without instance fields.
     * <p>
     * Steps:
     * 1. Create empty class instance
     * <p>
     * Result state:
     * - Returned size equals 0.
     * - No recursive traversal occurs.
     */
    @Test
    void testMemory_objectWithNoFields_returnZero() throws Exception {

        // Arrange
        class Empty {
        }

        Empty obj = new Empty();
        long expected = 0L;

        // Act
        long actual = MemoryMeasurer.measure(obj);

        // Assert
        assertEquals(expected, actual,
                "Object with no fields should have size 0");
    }

    /**
     * Testcase: Should prevent infinite recursion in cyclic object graphs.
     * <p>
     * Steps:
     * 1. Create two Node instances referencing each other.
     * 2. Execute memory measurement starting from one node.
     * <p>
     * Result state:
     * - Each unique object is counted exactly once.
     * - Infinite recursion does not occur.
     */
    @Test
    void testMemory_cyclicReferences_notDoubleCounted() throws Exception {

        //Arrange
        class Node {
            Node next;
        }

        ReferencesMemory measurer = new ReferencesMemory();
        Node a = new Node();
        Node b = new Node();
        a.next = b;
        b.next = a;
        long expected = 16;

        //Act
        long actual = measurer.measure(a);

        //Assert
        assertEquals(expected, actual,
                "Cyclic graph should not be double-counted (visited must prevent recursion loop)");
    }

    /**
     * Testcase: Should not double-count shared object references.
     * <p>
     * 1. Create Holder instance with two fields referencing the same object.
     * <p>
     * Result state:
     * - Holder shallow size is counted.
     * - Shared referenced object is measured only once.
     */
    @Test
    void testMemory_sharedReferences_notDoubleCounted() throws Exception {

        //Arrange
        class A {
            long x;
        }

        class Holder {
            A a1;
            A a2;
        }

        ReferencesMemory measure = new ReferencesMemory();
        A shared = new A();
        Holder holder = new Holder();
        holder.a1 = shared;
        holder.a2 = shared;
        long expected = 24;

        //Act
        long actual = measure.measure(holder);

        //Assert
        assertEquals(expected, actual,
                "Same referenced object should be counted once even if reachable via multiple fields");
    }
}