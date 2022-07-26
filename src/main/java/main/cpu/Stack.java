package main.cpu;

public class Stack {
    /**
     * The stack contains a collection of 16bit values, representing addresses in memory
     * Since there is no unsigned numeric type in java except for char, which is 16bit,
     * I'm using char values to represent 16bit unsigned integers.
     */
    private char[] array = new char[16];
    private int pointer = -1;

    public void setAddress(char address) {
        pointer++;
        array[pointer] = address;
    }

    public char getAddress() {
        char address = array[pointer];
        pointer--;
        return address;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }
}
