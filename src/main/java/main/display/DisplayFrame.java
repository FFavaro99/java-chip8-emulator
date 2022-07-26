package main.display;

import main.keyboard.Keyboard;

import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JFrame {
    private final DisplayModel model;
    private int multiplier;
    private int size;

    private static final Color PRIMARY_COLOUR = new Color(175, 129, 220);
    private static final Color SECONDARY_COLOUR = new Color(41, 50, 95);

    public DisplayFrame(int multiplier) {
        model = new DisplayModel();
        this.multiplier = multiplier;


        this.size = multiplier - multiplier/15;

        this.setBackground(new Color(45, 26, 64));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.setName("Chip8 - Emulator by Federico Favaro");
        this.setTitle("Chip8 - Emulator by Federico Favaro");
        this.addKeyListener(new Keyboard());

        int width = 64 * multiplier + this.getInsets().left + this.getInsets().right;
        int height = 32 * multiplier + this.getInsets().top + this.getInsets().bottom;
        this.setSize(width, height);
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        for (int x = 0; x < 64; x++)
            for (int y = 0; y < 32; y++) {
                Coordinate coord = new Coordinate(x, y);
                if (model.getBit(coord) == 1) drawAt(coord, graphics2D);
                else drawBackroundAt(coord, graphics2D);
            }
    }

    public void drawAt(Coordinate coord, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(PRIMARY_COLOUR);
        g2d.fillRect(coord.getX()*multiplier+this.getInsets().left, coord.getY()*multiplier+this.getInsets().top, size, size);
    }

    public void drawBackroundAt(Coordinate coord, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(SECONDARY_COLOUR);
        g2d.fillRect(coord.getX()*multiplier+this.getInsets().left, coord.getY()*multiplier+this.getInsets().top, size, size);
    }

    public boolean drawSprite(Coordinate coord, char[] sprite) {
        boolean collision = model.drawSprite(coord, sprite);
        repaint();
        return collision;
    }

    public void clear() {
        model.clear();
    }

    public char getKeyPressed() {
        Keyboard keyboard = (Keyboard) getKeyListeners()[0];
        return keyboard.getCurrentKeyPressed();
    }


}
