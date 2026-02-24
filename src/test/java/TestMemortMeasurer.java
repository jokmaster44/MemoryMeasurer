import org.example.MemoryMeasurer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMemortMeasurer {

    static class OnlyInts {
        int a;
        int b;
        int c;
    }

    @Test
    void TestMemory_Should_Measure_Three_Ints() {
        OnlyInts obj = new OnlyInts();

        long size = MemoryMeasurer.measure(obj);

        assertEquals(12, size);
    }

    static class OnlyLongs {
        long a;
        long b;
        long c;
    }

    @Test
    void TestMemory_Should_Measure_Three_Long() {
        OnlyLongs obj = new OnlyLongs();

        long size = MemoryMeasurer.measure(obj);

        assertEquals(24, size);
    }

    static class WithNullReference {
        int a;
        Object ref;
    }

    @Test
    void TestMemory_should_Not_Add_Extra_For_Null_Reference() {
        WithNullReference obj = new WithNullReference();
        long size = MemoryMeasurer.measure(obj);

    assertEquals(4 + 8, size);
    }

    static class Node{
        Node next;
    }

    @Test
    void TestMemory_Should_Handle_Cyclic_Reference(){
        Node a = new Node();
        Node b = new Node();

        a.next = a;
        b.next = b;

        long size = MemoryMeasurer.measure(a);

        assertEquals(16, size);
    }

}