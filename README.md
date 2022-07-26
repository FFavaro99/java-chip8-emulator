# java-chip8-emulator

Java Chip8 emulator with no sound (YET!) implementing all of the OPcodes defined in the original CHIP8. This emulator passes all of the opcodes tests I could find online:
- https://github.com/corax89/chip8-test-rom
- https://github.com/offstatic/chiptest

## How to Run

To run this emulator you will need maven to compile the source code into executable, and java to run the executable.
You will have to do the following:
- clone this github repository
- move into the cloned folder
- run the command `mvn package`
- finally, run the emulator with `java -cp target/chip8-interpreter-1.0-SNAPSHOT.jar main.Chip8`, specifying the absolute path to a chip8 rom as command-line argument. For example, you may run `java -cp target/chip8-interpreter-1.0-SNAPSHOT.jar main.Chip8 "D:/chip8/roms/Space Invaders.ch8"`

## How to Play

The CHIP8 has a total of 15 keys, each representing a hexidecimal value. These keys are organized as such:
```
|1|2|3|C|
---------
|4|5|6|D|
---------
|7|8|9|E|
---------
|A|0|B|F|
```
In this emulator, these keys correspond to:
```
|1|2|3|4|
---------
|Q|W|E|R|
---------
|A|S|D|F|
---------
|Z|X|C|V|
```
Each game uses keys as it pleases, so you'll have to play around with these keys to find out what does what. For space invaders, for example, you move with `Q` and `E` and shoot with `W`
