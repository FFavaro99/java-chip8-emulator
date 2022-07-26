package main.display;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Array;

import static org.junit.jupiter.api.Assertions.*;

class DisplayModelTest {
    private DisplayModel displayModel;
    @BeforeEach
    public void before() {
        displayModel = new DisplayModel();
    }

    @Nested
    public class IndexToCoordinateTests {
        @Test
        public void indexToCoordsTest() {
            int index = 67;
            Coordinate expectedCoordinate = new Coordinate(3, 1);
            Coordinate actualCoordinate = displayModel.indexToCoords(index);
            assertTrue(expectedCoordinate.equals(actualCoordinate));
        }

        @Test
        public void indexToCoordsMinTest() {
            int index = 0;
            Coordinate expectedCoordinate = new Coordinate(0, 0);
            Coordinate actualCoordinate = displayModel.indexToCoords(index);
            assertTrue(expectedCoordinate.equals(actualCoordinate));
        }

        @Test
        public void indexToCoordsMaxTest() {
            int index = 2047;
            Coordinate expectedCoordinate = new Coordinate(63, 31);
            Coordinate actualCoordinate = displayModel.indexToCoords(index);
            assertTrue(expectedCoordinate.equals(actualCoordinate));
        }

        @Test
        public void outOfBoundIndexTest() {
            int index = 5000;
            assertThrows(IndexOutOfBoundsException.class,
                    () -> displayModel.indexToCoords(index));
        }
    }

    @Nested
    public class CoordinateToIndexTests {
        @Test
        public void coordinateToIndexTest() {
            var coords = new Coordinate(5, 3);
            int expectedIndex = 197;
            int actualIndex = displayModel.coordsToIndex(coords);
            assertEquals(expectedIndex, actualIndex);
        }

        @Test
        public void coordinateToIndexMinTest() {
            var coords = new Coordinate(0, 0);
            int expectedResult = 0;
            int actualIndex = displayModel.coordsToIndex(coords);
            assertEquals(expectedResult, actualIndex);
        }

        @Test
        public void coordinateToIndexMaxTest() {
            var coords = new Coordinate(63, 31);
            int expectedIndex = 2047;
            int actualIndex = displayModel.coordsToIndex(coords);
            assertEquals(expectedIndex, actualIndex);
        }

        @Test
        public void coordinateToIndexOOBTest() {
            var coords1 = new Coordinate(-1, -2);
            assertThrows(IndexOutOfBoundsException.class,
                    () -> displayModel.coordsToIndex(coords1));
        }

        @Test
        public void coordinateToIndexWrapAroundTest() {
            var coords = new Coordinate(65, 0);
            int expectedIndex = 1;
            int actualIndex = displayModel.coordsToIndex(coords);
            assertEquals(expectedIndex, actualIndex);
        }
    }

    @Nested
    public class DrawSpriteTest {
        @Test
        public void drawSpriteOriginTest() {
            char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
            Coordinate origin = new Coordinate(0, 0);
            boolean collision = displayModel.drawSprite(origin, sprite);

            int[] control = {1, 1, 1, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                             1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                             1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                             1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                             1, 1, 1, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
            };

            int[] myArray = new int[control.length];
            for (int i = 0; i < myArray.length; i++) {
                myArray[i] = displayModel.getBit(i);
            }

            assertFalse(collision);
            assertArrayEquals(control, myArray);

        }

        @Test
        public void drawSpriteWrapAroundTest() {
            char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
            Coordinate coord = new Coordinate(61, 1);
            boolean collision = displayModel.drawSprite(coord, sprite);

            int[] control = {
                    0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,
                    1, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,1,1,
                    1, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,1,1,
            };

            int[] myArray = new int[control.length];
            for (int i = 0; i < myArray.length; i++) {
                myArray[i] = displayModel.getBit(i);
            }

            assertFalse(collision);
            assertArrayEquals(control, myArray);

        }
    }

    @Test
    public void clearDisplayTest() {
        char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
        Coordinate origin = new Coordinate(0, 0);
        displayModel.drawSprite(origin, sprite);
        displayModel.clear();

        char[] control = new char[2048];
        for (int i = 0; i < control.length; i++) {
            assertEquals(control[i], displayModel.getBit(i));
        }
    }

}