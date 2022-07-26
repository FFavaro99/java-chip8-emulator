package main.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Memory {
    char[] memoryArray;

    public Memory() {
        this.memoryArray = new char[4096];
        initSprites();
    }

    public void loadGame(String gamePath) throws IOException {
        byte[] arr;
        File game = new File(gamePath);
        try (FileInputStream inputStream = new FileInputStream(game)) {
            arr = inputStream.readAllBytes();
        }
        for (int i = 0; i < arr.length; i++) {
            memoryArray[i + 0x200] = (char) (arr[i] & 0xFF);
        }
    }

    private void initSprites() {
        char[] sprites = {
                0xf0, 0x90, 0x90, 0x90, 0xf0,
                0x20, 0x60, 0x20, 0x20, 0x70,
                0xf0, 0x10, 0xf0, 0x80, 0xf0,
                0xf0, 0x10, 0xf0, 0x10, 0xf0,
                0x90, 0x90, 0xf0, 0x10, 0x10,
                0xf0, 0x80, 0xf0, 0x10, 0xf0,
                0xf0, 0x80, 0xf0, 0x90, 0xf0,
                0xf0, 0x10, 0x20, 0x40, 0x40,
                0xf0, 0x90, 0xf0, 0x90, 0xf0,
                0xf0, 0x90, 0xf0, 0x10, 0xf0,
                0xf0, 0x90, 0xf0, 0x90, 0xf0,
                0xe0, 0x90, 0xe0, 0x90, 0xe0,
                0xf0, 0x80, 0x80, 0x80, 0xf0,
                0xe0, 0x90, 0x90, 0x90, 0xe0,
                0xf0, 0x80, 0xf0, 0x80, 0xf0,
                0xf0, 0x80, 0xf0, 0x80, 0x80
        };
        for (int i = 0; i < sprites.length; i++) {
            memoryArray[i] = sprites[i];
        }
    }

    public char readByte(int index) {
        return memoryArray[index];
    }

    public char readInstruction(int index) {
        char p1 = memoryArray[index];
        char p2 = memoryArray[index+1];
        return (char) ( (p1 << 8) | p2);
    }

    public void writeByte(int index, char b) {
        memoryArray[index] = b;
    }


}
