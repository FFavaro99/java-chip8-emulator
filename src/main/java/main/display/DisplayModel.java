package main.display;

public class DisplayModel {
    private int[] display;

    public DisplayModel() {
        this.display = new int[2048];
    }

    public Coordinate indexToCoords(int index) {
        if (index >= 2048 || index < 0)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for DisplayModel");
        int y = index / 64;
        int x = index % 64;
        return new Coordinate(x, y);
    }

    public int coordsToIndex(Coordinate coordinate) {
        int x = coordinate.getX() % 64;
        if (x < 0)
            throw new IndexOutOfBoundsException("Coordinate X is not valid");
        int y = coordinate.getY();
        if (y > 31 || y < 0)
            throw new IndexOutOfBoundsException("Coordinate Y is not valid");
        return (y * 64) + x;
    }

    public boolean drawSprite(Coordinate coordinate, char[] sprite) {
        boolean collision = false;
        for (int i = 0; i < sprite.length; i++) {
            int y = coordinate.getY() + i;
            if (y >= 32 || y < 0) break;
            collision = drawByte(new Coordinate(coordinate.getX(), y), sprite[i]);
        }
        return collision;
    }

    private boolean drawByte(Coordinate coordinate, char b) {
        b = (char) (b & 0x00FF);
        char lsbMask = 0x0001;

        boolean collision = false;
        for (int i = 0; i < 8; i++) {
            Coordinate tempCoord = new Coordinate(coordinate.getX(), coordinate.getY());
            tempCoord.setX(coordinate.getX() + i);
            int index = coordsToIndex(tempCoord);
            char currentB = (char) ((b >>> (7-i)) & lsbMask);
            int displayB = display[index];
            if (displayB == 1 && currentB == 1) collision = true;
            display[index] = currentB ^ displayB;
        }
        return collision;
    }

    public int getBit(int index) {
        return display[index];
    }

    public int getBit(Coordinate coord) {
        return display[coordsToIndex(coord)];
    }

    public void clear() {
        for (int i = 0; i < display.length; i++)
            display[i] = 0;
    }

}