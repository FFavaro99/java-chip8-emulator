package main.display;

import static org.junit.jupiter.api.Assertions.*;

class DisplayFrameTest {

    public static void main(String[] args) {
        DisplayModel displayModel = new DisplayModel();
        DisplayFrame df = new DisplayFrame(15);
        char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
        char[] sprite1 = {0xf0, 0x10, 0xf0, 0x10, 0xf0};
        Coordinate origin = new Coordinate(0, 0);
        Coordinate middle = new Coordinate(34, 13);
        Coordinate wrap = new Coordinate(62, 23);
        df.drawSprite(origin, sprite);
        df.drawSprite(middle, sprite1);
        df.drawSprite(wrap, sprite1);
    }




}