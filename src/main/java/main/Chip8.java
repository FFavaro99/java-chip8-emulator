package main;

import main.cpu.CPU;
import main.cpu.Stack;
import main.display.DisplayFrame;
import main.memory.Memory;

import java.io.IOException;

public class Chip8 {
    public static void main(String... args) {
        if (args.length != 0) {
            String gamePath = args[0];
            Memory memory = new Memory();
            boolean gameFound = true;
            try {
                memory.loadGame(gamePath);
            } catch (IOException e) {
                gameFound = false;
            }
            if (gameFound) {
                int resolutionMultiplier = 15;
                if (args.length != 1) {
                    resolutionMultiplier = Integer.parseInt(args[1]);
                }
                DisplayFrame frame = new DisplayFrame(resolutionMultiplier);
                CPU cpu = new CPU(new Stack(), memory, frame);

                cpu.run();
            }
            else System.out.println("No game with name " + args[0] + " was found");
        }
        else System.out.println("No game path was specified");
    }
}
