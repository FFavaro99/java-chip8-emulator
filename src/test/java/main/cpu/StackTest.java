package main.cpu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {
    private Stack stack;

    @BeforeEach
    public void before() {
        stack = new Stack();
    }

    @Test
    public void initialSetAddressTest() {
        stack.setAddress((char)0x0F0F);

        assertEquals(0, stack.getPointer());
    }

    @Test
    public void multipleSetAddressTest() {
        stack.setAddress((char)0x0F0F);
        stack.setAddress((char)0x0A0A);
        stack.setAddress((char)0x0B3F);

        assertEquals(2, stack.getPointer());
    }

    @Test
    public void getAddressExceptionTest() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> stack.getAddress());
    }

    @Test
    public void multipleGetAddressTest() {
        stack.setAddress((char)0x0F0F);
        stack.setAddress((char)0x0A0A);
        stack.setAddress((char)0x0B3F);

        assertEquals((char)0x0B3F, stack.getAddress());
        assertEquals((char)0x0A0A, stack.getAddress());
        assertEquals((char)0x0F0F, stack.getAddress());
    }

}