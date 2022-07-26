package main.keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
    private char currentKeyPressed = 0xFFFF;

    @Override
    public void keyTyped(KeyEvent e) {
        keyPressed(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        currentKeyPressed = convertToHex(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (convertToHex(e) == currentKeyPressed)
            currentKeyPressed = 0xFFFF;
    }

    private char convertToHex(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '1': return 0x1;
            case '2': return 0x2;
            case '3': return 0x3;
            case '4': return 0xC;
            case 'q': return 0x4;
            case 'w': return 0x5;
            case 'e': return 0x6;
            case 'r': return 0xD;
            case 'a': return 0x7;
            case 's': return 0x8;
            case 'd': return 0x9;
            case 'f': return 0xE;
            case 'z': return 0xA;
            case 'x': return 0x0;
            case 'c': return 0xB;
            case 'v': return 0xF;
            default: return 0xFFFF;
        }
    }

    public char getCurrentKeyPressed() {
        return currentKeyPressed;
    }


}
