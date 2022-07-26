package main.cpu;

import main.display.Coordinate;
import main.display.DisplayFrame;
import main.memory.Memory;

import java.util.Random;

/**
 * Class with the responsibility of modifying the state of the application.
 * This is accomplished through methods that fetch, decode and execute opcodes.
 */
public class CPU {
    private final Stack stack;
    private final char[] registers;

    private char iRegister;
    private char delayTimer;
    private char soundTimer;
    private char programCounter;

    private final Memory memory;
    private final DisplayFrame display;

    public CPU (Stack stack, Memory memory, DisplayFrame display) {
        this.stack = stack;
        this.memory = memory;
        this.display = display;
        registers = new char[16];
        iRegister = 0;
        delayTimer = 0;
        soundTimer = 0;
        programCounter = 0x200;
    }

    /**
     * Decodes an instruction to make it readable by the executeOpcode() method
     * @param instruction 16-bit long instruction, read from memory array
     * @return instruction classified by type, to be used by CPU.executeOpcode(Opcode)
     */
    public Opcode decodeInstruction(char instruction) {
        if ((instruction == 0x00E0))
            return new Opcode(Opcode.OpcodeType.CLS, instruction);
        if ((instruction == 0x00EE))
            return new Opcode(Opcode.OpcodeType.RET, instruction);
        switch(instruction & 0xF000) {
            case 0x1000: return new Opcode(Opcode.OpcodeType.JP_ADDR, instruction);
            case 0x2000: return new Opcode(Opcode.OpcodeType.CALL_ADDR, instruction);
            case 0x3000: return new Opcode(Opcode.OpcodeType.SE_VX_BYTE, instruction);
            case 0x4000: return new Opcode(Opcode.OpcodeType.SNE_VX_BYTE, instruction);
            case 0x5000: return new Opcode(Opcode.OpcodeType.SE_VX_VY, instruction);
            case 0x6000: return new Opcode(Opcode.OpcodeType.LD_VX_BYTE, instruction);
            case 0x7000: return new Opcode(Opcode.OpcodeType.ADD_VX_BYTE, instruction);
            case 0x8000:
                switch(instruction & 0x000F) {
                    case 0x0000: return new Opcode(Opcode.OpcodeType.LD_VX_VY, instruction);
                    case 0x0001: return new Opcode(Opcode.OpcodeType.OR_VX_VY, instruction);
                    case 0x0002: return new Opcode(Opcode.OpcodeType.AND_VX_VY, instruction);
                    case 0x0003: return new Opcode(Opcode.OpcodeType.XOR_VX_VY, instruction);
                    case 0x0004: return new Opcode(Opcode.OpcodeType.ADD_VX_VY, instruction);
                    case 0x0005: return new Opcode(Opcode.OpcodeType.SUB_VX_VY, instruction);
                    case 0x0006: return new Opcode(Opcode.OpcodeType.SHR_VX_VY, instruction);
                    case 0x0007: return new Opcode(Opcode.OpcodeType.SUBN_VX_VY, instruction);
                    case 0x000E: return new Opcode(Opcode.OpcodeType.SHL_VX_VY, instruction);
                    default: return new Opcode(Opcode.OpcodeType.UNSUPPORTED, instruction);
                }
            case 0x9000: return new Opcode(Opcode.OpcodeType.SNE_VX_VY, instruction);
            case 0xA000: return new Opcode(Opcode.OpcodeType.LD_I_ADDR, instruction);
            case 0xB000: return new Opcode(Opcode.OpcodeType.JP_V0_ADDR, instruction);
            case 0xC000: return new Opcode(Opcode.OpcodeType.RND_VX_BYTE, instruction);
            case 0xD000: return new Opcode(Opcode.OpcodeType.DRW_VX_VY_NIBBLE, instruction);
            case 0xE000:
                switch(instruction & 0x00FF) {
                    case 0X009E: return new Opcode(Opcode.OpcodeType.SKP_VX, instruction);
                    case 0X00A1: return new Opcode(Opcode.OpcodeType.SKNP_VX, instruction);
                    default: return new Opcode(Opcode.OpcodeType.UNSUPPORTED, instruction);
                }
            case 0xF000:
                switch(instruction & 0x00FF) {
                    case 0x0007: return new Opcode(Opcode.OpcodeType.LD_VX_DT, instruction);
                    case 0x000A: return new Opcode(Opcode.OpcodeType.LD_VX_K, instruction);
                    case 0x0015: return new Opcode(Opcode.OpcodeType.LD_DT_VX, instruction);
                    case 0x0018: return new Opcode(Opcode.OpcodeType.LD_ST_VX, instruction);
                    case 0X001E: return new Opcode(Opcode.OpcodeType.ADD_I_VX, instruction);
                    case 0X0029: return new Opcode(Opcode.OpcodeType.LD_F_VX, instruction);
                    case 0X0033: return new Opcode(Opcode.OpcodeType.LD_B_VX, instruction);
                    case 0X0055: return new Opcode(Opcode.OpcodeType.LD_I_VX, instruction);
                    case 0X0065: return new Opcode(Opcode.OpcodeType.LD_VX_I, instruction);
                    default: return new Opcode(Opcode.OpcodeType.UNSUPPORTED, instruction);
                }
            default: return new Opcode(Opcode.OpcodeType.UNSUPPORTED, instruction);
        }
    }

    /**
     * Executes an instruction. This implies changes in the state of the emulator and possibly rendering
     * @param opcode represents a decoded instruction
     */
    public void executeOpcode(Opcode opcode) {
        switch(opcode.getType()) {
            case CLS: display.clear(); break;
            case RET: returnAddress(); break;
            case JP_ADDR: jumpAddress(opcode.getInstruction()); break;
            case CALL_ADDR: callAddress(opcode.getInstruction()); break;
            case SE_VX_BYTE: skipIfEqualVxByte(opcode.getInstruction()); break;
            case SNE_VX_BYTE: skipIfNotEqualVxByte(opcode.getInstruction()); break;
            case SE_VX_VY: skipIfEqualVxVy(opcode.getInstruction()); break;
            case LD_VX_BYTE: ldVxByte(opcode.getInstruction()); break;
            case ADD_VX_BYTE: addVxByte(opcode.getInstruction()); break;
            case LD_VX_VY: ldVxVy(opcode.getInstruction()); break;
            case OR_VX_VY: orVxVy(opcode.getInstruction()); break;
            case AND_VX_VY: andVxVy(opcode.getInstruction()); break;
            case XOR_VX_VY: xorVxVy(opcode.getInstruction()); break;
            case ADD_VX_VY: addVxVy(opcode.getInstruction()); break;
            case SUB_VX_VY: subVxVy(opcode.getInstruction()); break;
            case SHR_VX_VY: shrVxVy(opcode.getInstruction()); break;
            case SUBN_VX_VY: subnVxVy(opcode.getInstruction()); break;
            case SHL_VX_VY: shlVxVy(opcode.getInstruction()); break;
            case SNE_VX_VY: sneVxVy(opcode.getInstruction()); break;
            case LD_I_ADDR: ldIAddr(opcode.getInstruction()); break;
            case JP_V0_ADDR: jpV0Addr(opcode.getInstruction()); break;
            case RND_VX_BYTE: rndVxByte(opcode.getInstruction()); break;
            case DRW_VX_VY_NIBBLE: drawVxVyNibble(opcode.getInstruction()); break;
            case SKP_VX: skpVx(opcode.getInstruction()); break;
            case SKNP_VX: sknpVx(opcode.getInstruction()); break;
            case LD_VX_DT: ldVxDt(opcode.getInstruction()); break;
            case LD_VX_K: ldVxK(opcode.getInstruction()); break;
            case LD_DT_VX: ldDtVx(opcode.getInstruction()); break;
            case LD_ST_VX: ldStVx(opcode.getInstruction()); break;
            case ADD_I_VX: addIVx(opcode.getInstruction()); break;
            case LD_F_VX: ldFVx(opcode.getInstruction()); break;
            case LD_B_VX: ldBVx(opcode.getInstruction()); break;
            case LD_I_VX: ldIVx(opcode.getInstruction()); break;
            case LD_VX_I: ldVxI(opcode.getInstruction()); break;
            default: break;
        }
    }

    public void run() {
        //0. check if it's time to execute instruction
        long lastExecutionTime = System.currentTimeMillis();
        long lastTimerUpdate = lastExecutionTime;
        boolean skipTimer = false;

        while(true) {
            if (skipTimer || System.currentTimeMillis() - lastExecutionTime >= 2) {
                lastExecutionTime = System.currentTimeMillis();
                //1. fetch instruction
                char instruction = memory.readInstruction(programCounter);
                programCounter += 2;
                //2. decode instruction
                Opcode opcode = decodeInstruction(instruction);
                if (opcode.getType() == Opcode.OpcodeType.UNSUPPORTED) {
                    System.out.println("OPCODE NOT SUPPORTED: " + (int)opcode.getInstruction());
                    break;
                }
                //3. execute instruction
                executeOpcode(opcode);
                if (System.currentTimeMillis() - lastExecutionTime > 4) {
                    skipTimer = true;
                    System.out.println("Skipping timer on next execution");
                }
                else skipTimer = false;
            }
            //4. update timers
            if (System.currentTimeMillis() - lastTimerUpdate >= 16.6) {
                lastTimerUpdate = System.currentTimeMillis();
                if (soundTimer > 0) {
                    //TODO make sound
                    soundTimer--;
                }
                if (delayTimer > 0) delayTimer--;
            }
        }


    }

    private void returnAddress() {
        programCounter = stack.getAddress();
    }

    private void jumpAddress(char instruction) {
        char addr = (char) (instruction & 0x0FFF);
        programCounter = addr;

    }

    private void callAddress(char instruction) {
        char addr = (char) (instruction & 0x0FFF);
        stack.setAddress(programCounter);
        programCounter = addr;
    }

    private void skipIfEqualVxByte(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char b = (char) (instruction & 0x00FF);
        char vx = registers[x];
        if (vx == b) programCounter += 2;
    }

    private void skipIfNotEqualVxByte(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char b = (char) (instruction & 0x00FF);
        char vx = registers[x];
        if (vx != b) programCounter += 2;
    }

    private void skipIfEqualVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = registers[x];
        char vy = registers[y];
        if (vx == vy) programCounter += 2;
    }

    private void ldVxByte(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char b = (char) (instruction & 0x00FF);
        registers[x] = b;
    }

    private void addVxByte(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char b = (char) (instruction & 0x00FF);
        char vx = registers[x];
        char res = (char)((vx + b) & 0x00FF);
        registers[x] = res;
    }

    private void ldVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        registers[x] = registers[y];
    }

    private void orVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) ((registers[x] | registers[y]) & 0x00FF);
        registers[x] = vx;
    }

    private void andVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) ((registers[x] & registers[y]) & 0x00FF);
        registers[x] = vx;
    }

    private void xorVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) ((registers[x] ^ registers[y]) & 0x00FF);
        registers[x] = vx;
    }

    private void addVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) (registers[x] + registers[y]);
        if ((vx & 0xFF00) > 0) {
            registers[0x0F] = 0x01;
            vx &= 0X00FF;
        }
        else registers[0x0F] = 0x00;
        registers[x] = vx;
    }

    private void subVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) (registers[x] - registers[y]);
        if (registers[x] > registers[y]) {
            registers[0x0F] = 0x01;
        } else {
            registers[0x0F] = 0x00;
            vx = (char) (vx & 0x00FF);
        }
        registers[x] = vx;
    }

    private void shrVxVy(char instruction) {
        char x = (char)((instruction & 0x0F00) >>> 8);
        registers[0x0F] = (char) (registers[x] & 0b1);
        char vx = (char) (registers[x] >>> 1);
        registers[x] = vx;
    }

    private void subnVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char vx = (char) (registers[y] - registers[x]);
        if (registers[x] > registers[y]) {
            registers[0x0F] = 0x00;
            vx = (char) (vx & 0x00FF);
        } else {
            registers[0x0F] = 0x01;
        }
        registers[x] = vx;
    }

    private void shlVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        registers[0xF] = (char)( (registers[x] & 0b1000_0000) >>> 7);
        char vx = (char) ((registers[x] << 1) & 0x00FF);
        registers[x] = vx;
    }

    private void sneVxVy(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        if (registers[x] != registers[y]) programCounter += 2;
    }

    private void ldIAddr(char instruction) {
        char addr = (char) (instruction & 0x0FFF);
        iRegister = addr;
    }

    private void jpV0Addr(char instruction) {
        char addr = (char) (instruction & 0x0FFF);
        char v0 = registers[0];
        char result = (char) (addr + v0);
        programCounter = result;
    }

    private void rndVxByte(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char b = (char) (instruction & 0x00FF);
        char rnd = (char) new Random().nextInt(256);
        char res = (char) ((rnd & b) & 0x00FF);
        registers[x] = res;
    }

    private void drawVxVyNibble(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char y = (char) ((instruction & 0x00F0) >>> 4);
        char nibble = (char) (instruction & 0x000F);
        char vx = registers[x];
        char vy = registers[y];
        Coordinate coord = new Coordinate(vx, vy);
        char[] sprite = new char[nibble];
        for (int i = 0; i < nibble; i++) {
            sprite[i] = memory.readByte(i + iRegister);
        }
        boolean collision = display.drawSprite(coord, sprite);
        if (collision) registers[0xf] = 0x1;
        else registers[0xf] = 0x0;
    }

    private void skpVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char vx = registers[x];
        char keyPressed = display.getKeyPressed();
        if (keyPressed != 0xFFFF && keyPressed == vx) programCounter += 2;
    }

    private void sknpVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char vx = registers[x];
        char keyPressed = display.getKeyPressed();
        if (keyPressed == 0XFFFF || keyPressed != vx) programCounter += 2;
    }

    private void ldVxDt(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        registers[x] = delayTimer;
    }

    private void ldVxK(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        boolean keySet = false;
        while (!keySet) {
            char key = display.getKeyPressed();
            if (key != 0xFFFF) {
                registers[x] = key;
                keySet = true;
            }
            else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Exception occurred in LD_VX_DT");
                }
            }
        }
    }

    private void ldDtVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        delayTimer = registers[x];
    }

    private void ldStVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        soundTimer = registers[x];
    }

    private void addIVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char vx = registers[x];
        char res = (char) ((iRegister + vx) & 0x0FFF);
        iRegister = res;
    }

    private void ldFVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char vx = registers[x];
        iRegister = (char) (vx * 5);
    }

    private void ldBVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        char vx = registers[x];
        memory.writeByte(iRegister, (char)(vx/100));
        vx = (char)(vx % 100);
        memory.writeByte(iRegister + 1, (char)(vx/10));
        vx = (char)(vx % 10);
        memory.writeByte(iRegister + 2, vx);
    }

    private void ldIVx(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        for (int i = 0; i <= x; i++) {
            memory.writeByte(i + iRegister, registers[i]);
        }
    }

    private void ldVxI(char instruction) {
        char x = (char) ((instruction & 0x0F00) >>> 8);
        for (int i = 0; i <= x; i++) {
            registers[i] = memory.readByte(i + iRegister);
        }
    }

    //GETTERS AND SETTERS

    public char getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(char programCounter) {
        this.programCounter = programCounter;
    }

    public char[] getRegisters() {
        return registers;
    }

    public char getIRegister() {
        return iRegister;
    }

    public char getDelayTimer() {
        return delayTimer;
    }

    public void setDelayTimer(char c) {
        delayTimer = c;
    }

    public char getSoundTimer() {
        return soundTimer;
    }
    public void setSoundTimer(char c) { soundTimer = c;}

    public void setIRegister(char c) {iRegister = c;}
}
