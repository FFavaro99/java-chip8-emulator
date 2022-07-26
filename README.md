# java-chip8-emulator

Java Chip8 emulator with no sound (YET!) implementing all of the OPcodes defined in the original CHIP8. This emulator passes all of the opcodes tests I could find online:
- https://github.com/corax89/chip8-test-rom
- https://github.com/offstatic/chiptest

## How To Run

To run this emulator you will need maven to compile the source code into executable, and java to run the executable.
You will have to do the following:
- clone this github repository
- move into the cloned folder
- run the command `mvn package`
- finally, run the emulator with `java -cp target/chip8-interpreter-1.0-SNAPSHOT.jar main.Chip8`, specifying the absolute path to a chip8 rom as command-line argument. For example, you may run `java -cp target/chip8-interpreter-1.0-SNAPSHOT.jar main.Chip8 "D:/chip8/roms/Space Invaders.ch8"`
