package main.cpu;

import main.display.DisplayFrame;
import main.display.DisplayModel;
import main.memory.Memory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {
    private CPU cpu;
    private Stack stack;
    private Memory memory;
    private DisplayFrame displayFrame = new DisplayFrame(15);

    @BeforeEach
    public void before() {
        stack = new Stack();
        memory = new Memory();
        cpu = new CPU(stack, memory, displayFrame);
    }

    @AfterEach
    public void tearDown() {
        stack = null;
        memory = null;
        cpu = null;
    }

    @Nested
    public class JumpAddressTests {
        @Test
        public void jumpAddressDecodingTest() {
            char instruction = 0x1ABC;
            Opcode o = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.JP_ADDR, o.getType());
        }

        @Test
        public void jumpAddressExecutionTest() {
            char instruction = 0x1ABC;
            Opcode o = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(o);

            assertEquals(0x0ABC, cpu.getProgramCounter());
        }

        @Test
        public void maxJumpAddressExecutionTest() {
            char instruction = 0x1FFF;
            Opcode o = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(o);

            assertEquals(0x0FFF, cpu.getProgramCounter());
        }

        @Test
        public void minJumpAddressExecutionTest() {
            char instruction = 0x1000;
            Opcode o = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(o);

            assertEquals( 0x0000, cpu.getProgramCounter());
        }
    }

    @Nested
    public class CallAddressTest {
        @Test
        public void callAddressDecodingTest() {
            char instruction = 0x2A3E;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.CALL_ADDR, opcode.getType());
        }

        @Test
        public void callAddressExecutionTest() {
            char instruction = 0x2A3E;
            Opcode opcode = cpu.decodeInstruction(instruction);

            cpu.executeOpcode(opcode);
            assertEquals(0x200, stack.getAddress());
            assertEquals(0x0A3E, cpu.getProgramCounter());
        }

        @Test
        public void maxCallAddressExecutionTest() {
            char instruction = 0x2FFF;
            Opcode opcode = cpu.decodeInstruction(instruction);

            cpu.executeOpcode(opcode);
            assertEquals(0x200, stack.getAddress());
            assertEquals(0x0FFF, cpu.getProgramCounter());
        }

        @Test
        public void minCallAddressExecutionTest() {
            char instruction = 0x2000;
            Opcode opcode = cpu.decodeInstruction(instruction);

            cpu.executeOpcode(opcode);
            assertEquals(0x200, stack.getAddress());
            assertEquals(0x0000, cpu.getProgramCounter());
        }
    }

    @Nested
    public class ReturnFromSubroutineTest {

        @Test
        public void returnDecodingTest() {
            char instruction = 0x00EE;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.RET, opcode.getType());
        }

        @Test
        public void returnExecutionTest() {
            char callAddr = 0x2A3E;
            char callAddr2 = 0x2FFF;
            cpu.executeOpcode(cpu.decodeInstruction(callAddr));
            cpu.executeOpcode(cpu.decodeInstruction(callAddr2));

            char instruction = 0x00EE;
            Opcode opcode = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(opcode);

            assertEquals(0x0A3E, cpu.getProgramCounter());
        }

    }

    @Nested
    public class SkipIfEqualVxByteTest {

        @Test
        public void skipIfEqualDecodingTest() {
            char instruction = 0x3A00;

            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.SE_VX_BYTE, opcode.getType());
        }

        @Test
        public void skipIfEqualTrueExecutionTest() {
            char instruction = 0x3AFF;
            cpu.getRegisters()[0x000A] = 0x00FF;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(202, cpu.getProgramCounter());
        }

        @Test
        public void skipIfEqualFalseExecutionTest() {
            char instruction = 0x3AFF;
            cpu.getRegisters()[0x000A] = 0x00FE;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(200, cpu.getProgramCounter());
        }
    }

    @Nested
    public class SkipIfNotEqualVxByteTest {

        @Test
        public void skipIfNotEqualDecodingTest() {
            char instruction = 0x4A00;

            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.SNE_VX_BYTE, opcode.getType());
        }

        @Test
        public void skipIfEqualTrueExecutionTest() {
            char instruction = 0x4AFF;
            cpu.getRegisters()[0x000A] = 0x00FF;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(200, cpu.getProgramCounter());
        }

        @Test
        public void skipIfEqualFalseExecutionTest() {
            char instruction = 0x4AFF;
            cpu.getRegisters()[0x000A] = 0x00FE;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(202, cpu.getProgramCounter());
        }
    }

    @Nested
    public class SkipIfEqualVxVyTest {
        @Test
        public void skipIfEqualDecodingTest() {
            char instruction = 0x5AB0;

            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.SE_VX_VY, opcode.getType());
        }

        @Test
        public void skipIfEqualTrueExecutionTest() {
            char instruction = 0x5AB0;
            cpu.getRegisters()[0x000A] = 0x00FF;
            cpu.getRegisters()[0x000B] = 0x00FF;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(202, cpu.getProgramCounter());
        }

        @Test
        public void skipIfEqualFalseExecutionTest() {
            char instruction = 0x5AB0;
            cpu.getRegisters()[0x000A] = 0x00FF;
            cpu.getRegisters()[0x000B] = 0x00AB;
            cpu.setProgramCounter((char)200);

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(200, cpu.getProgramCounter());
        }
    }

    @Nested
    public class LdVxByteTest {

        @Test
        public void ldVxByteDecodingTest() {
            char instruction = 0x6ABC;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.LD_VX_BYTE, opcode.getType());
        }

        @Test
        public void ldVxByteExecutionTest() {
            char instruction = 0x6ABC;

            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(0x00BC, cpu.getRegisters()[0x000A]);
        }


    }

    @Nested
    public class AddVxByteTest {
        @Test
        public void addVxByteDecodingTest() {
            char instruction = 0x7000;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.ADD_VX_BYTE, opcode.getType());
        }

        @Test
        public void addVxByteExecutionTest() {
            char instruction = 0x7010;
            cpu.getRegisters()[0] = 0x0010;
            Opcode opcode = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(opcode);

            assertEquals(0x0020, cpu.getRegisters()[0]);
        }

        @Test
        public void addVxByteOverflowTest() {
            char instruction = 0x7010;
            cpu.getRegisters()[0] = 0x00F1;
            Opcode opcode = cpu.decodeInstruction(instruction);
            cpu.executeOpcode(opcode);

            assertEquals(0x0001, cpu.getRegisters()[0]);
        }
    }

    @Nested
    public class LdVxVyTest {

        @Test
        public void ldVxVyDecodingTest() {
            char instruction = 0x8F30;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.LD_VX_VY, opcode.getType());
        }

        @Test
        public void ldVxVyExecutionTest() {
            char x = 0;
            char y = 1;
            cpu.getRegisters()[x] = 10;
            cpu.getRegisters()[y] = 20;
            char instruction = 0x8010;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(20, cpu.getRegisters()[x]);
        }

    }

    @Nested
    public class OrVxVyTest {

        @Test
        public void orVxVyDecodingTest() {
            char instruction = 0x8AB1;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.OR_VX_VY, opcode.getType());
        }

        @Test
        public void orVxVyExecutionTest() {
            char x = 0;
            char y = 1;
            char vx = 0b0000_0000_0100_0111;
            char vy = 0b0000_0000_0011_1011;
            char expectedResult = 0b0000_0000_0111_1111;

            char instruction = 0x8011;

            cpu.getRegisters()[x] = vx;
            cpu.getRegisters()[y] = vy;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
        }
    }

    @Nested
    public class AndVxVyTest {

        @Test
        public void andVxVyDecodingTest() {
            char instruction = 0x8AB2;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.AND_VX_VY, opcode.getType());
        }

        @Test
        public void andVxVyExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b00101101;
            cpu.getRegisters()[y] = 0b01001011;
            char expectedResult = 0b00001001;

            char instruction = 0x8AB2;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
        }
    }

    @Nested
    public class XorVxVyTest {

        @Test
        public void xorVxVyDecodingTest() {
            char instruction = 0x8AB3;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.XOR_VX_VY, opcode.getType());
        }

        @Test
        public void xorVxVyExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b00101101;
            cpu.getRegisters()[y] = 0b01001011;
            char expectedResult = 0b01100110;

            char instruction = 0x8AB3;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
        }
    }

    @Nested
    public class AddVxVyTest {
        @Test
        public void addVxVyDecodingTest() {
            char instruction = 0x8AB4;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.ADD_VX_VY, opcode.getType());
        }

        @Test
        public void addVxVyNoCarryExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b00101101;
            cpu.getRegisters()[y] = 0b01001011;
            char expectedResult = 0b01111000;

            char instruction = 0x8AB4;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
            assertEquals(0x00, cpu.getRegisters()[0x0F]);
        }

        @Test
        public void addVxVyWithCarryExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b11101101;
            cpu.getRegisters()[y] = 0b01001011;
            char expectedResult = 0b00111000;

            char instruction = 0x8AB4;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
            assertEquals(0x01, cpu.getRegisters()[0x0F]);
        }
    }

    @Nested
    public class SubVxVyTest {
        @Test
        public void subVxVyDecodingTest() {
            char instruction = 0x8005;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.SUB_VX_VY, opcode.getType());
        }

        @Test
        public void subVxVyNoBorrowExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b01001011;
            cpu.getRegisters()[y] = 0b00101101;
            char expectedResult = 0b00011110;

            char instruction = 0x8AB5;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
            assertEquals(0x01, cpu.getRegisters()[0x0F]);
        }

        @Test
        public void subVxVyWithBorrowExecutionTest() {
            char x = 0x000A;
            char y = 0x000B;
            cpu.getRegisters()[x] = 0b00101101;
            cpu.getRegisters()[y] = 0b01001011;
            char expectedResult = 0b11100010;

            char instruction = 0x8AB5;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getRegisters()[x]);
            assertEquals(0x00, cpu.getRegisters()[0x0F]);
        }
    }

    @Nested
    public class ShrVxVyTest {
        @Test
        public void shrVxVyDecodingTest() {
            char instruction = 0x8AB6;
            Opcode opcode = cpu.decodeInstruction(instruction);

            assertEquals(Opcode.OpcodeType.SHR_VX_VY, opcode.getType());
        }

        @Test
        public void shrVxVyZeroLsbExecutionTest() {
            char x = 0x000A;

            cpu.getRegisters()[x] = 0b00101100;
            char expectedX = 0b00010110;

            char instruction = 0x8AB6;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(0x00, cpu.getRegisters()[0x0F]);
            assertEquals(expectedX, cpu.getRegisters()[x]);
        }

        @Test
        public void shrVxVyOneLsbExecutionTest() {
            char x = 0x000A;

            cpu.getRegisters()[x] = 0b00101101;
            char expectedX = 0b00010110;

            char instruction = 0x8AB6;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(0x01, cpu.getRegisters()[0x0F]);
            assertEquals(expectedX, cpu.getRegisters()[x]);
        }
    }

    @Nested
    public class SubnVxVyTest {
        @Test
        public void subnVxVyDecodingTest() {
            char instruction = 0x8007;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.SUBN_VX_VY, opcode.getType());
        }

        @Test
        public void subnVxVyNoBorrowExecutionTest() {
            char x = 0x05;
            char y = 0x0A;
            cpu.getRegisters()[x] = 0b00101101;
            cpu.getRegisters()[y] = 0b01001011;

            char expectedX = 0b00011110;
            char expectedF = 0x01;

            char instruction = 0x85A7;

            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(expectedX, cpu.getRegisters()[x]);
            assertEquals(expectedF, cpu.getRegisters()[0xF]);
        }

        @Test
        public void subnVxVyBorrowExecutionTest() {
            char x = 0x05;
            char y = 0x0A;
            cpu.getRegisters()[x] = 0b01001011;
            cpu.getRegisters()[y] = 0b00101101;

            char expectedX = 0b11100010;
            char expectedF = 0x0;

            char instruction = 0x85A7;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(expectedX, cpu.getRegisters()[x]);
            assertEquals(expectedF, cpu.getRegisters()[0xF]);
        }
    }

    @Nested
    public class ShlVxVyTest {
        @Test
        public void shlVxVyDecodingTest() {
            char instruction = 0x800E;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.SHL_VX_VY, opcode.getType());
        }

        @Test
        public void shlVxVyZeroExecutionTest() {
            char x = 0x0A;
            cpu.getRegisters()[x] = 0b0011_0110;
            char expectedX = 0b0110_1100;
            char expectedF = 0;
            char instruction = 0x8AEE;

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedF, cpu.getRegisters()[0XF]);
            assertEquals(expectedX, cpu.getRegisters()[x]);
        }

        @Test
        public void shlVxVyOneExecutionTest() {
            char x = 0x0A;
            cpu.getRegisters()[x] = 0b1011_0110;
            char expectedX = 0b0110_1100;
            char expectedF = 1;
            char instruction = 0x8AEE;

            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedF, cpu.getRegisters()[0XF]);
            assertEquals(expectedX, cpu.getRegisters()[x]);
        }
    }

    @Nested
    public class SneVxVyTest {
        @Test
        public void sneVxVyDecodingTest() {
            char instruction = 0x9000;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.SNE_VX_VY, opcode.getType());
        }

        @Test
        public void sneVxVySkipExecutionTest() {
            char x = 0x3;
            char y = 0x5;
            cpu.getRegisters()[x] = 0xAB;
            cpu.getRegisters()[y] = 0xE3;
            cpu.setProgramCounter((char)500);

            char instruction = 0x9350;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(502, cpu.getProgramCounter());
        }

        @Test
        public void sneVxVyNoSkipExecutionTest() {
            char x = 0x3;
            char y = 0x5;
            cpu.getRegisters()[x] = 0x1F;
            cpu.getRegisters()[y] = 0x1F;
            cpu.setProgramCounter((char)500);

            char instruction = 0x9350;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(500, cpu.getProgramCounter());
        }
    }

    @Nested
    public class LdIAddrTest {
        @Test
        public void ldIAddrDecodingTest() {
            char instruction = 0xA000;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.LD_I_ADDR, opcode.getType());
        }

        @Test
        public void ldIAddrExecutionTest() {
            char instruction = 0xA123;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));
            assertEquals(0x0123, cpu.getIRegister());
        }
    }

    @Nested
    public class JpV0AddrTest {
        @Test
        public void jpV0AddrDecodingTest() {
            char instruction = 0xBABC;
            Opcode opcode = cpu.decodeInstruction(instruction);
            assertEquals(Opcode.OpcodeType.JP_V0_ADDR, opcode.getType());
        }

        @Test
        public void jpV0AddrExecutionTest() {
            char n = 0x0ABC;
            char v0 = 0x00EB;
            cpu.getRegisters()[0] = v0;
            char expectedResult = (char) (n + v0);

            char instruction = 0xBABC;
            cpu.executeOpcode(cpu.decodeInstruction(instruction));

            assertEquals(expectedResult, cpu.getProgramCounter());
        }
    }

    @Nested
    public class RndVxByteTest {
        @Test
        public void rndVxByteDecodingTest() {
            char inst = 0xC000;
            Opcode opcode = cpu.decodeInstruction(inst);
            assertEquals(Opcode.OpcodeType.RND_VX_BYTE, opcode.getType());
        }

        @RepeatedTest(50)
        public void rndVxByteExecutionTest() {
            char x = 5;
            char inst = 0xC5FF;
            cpu.executeOpcode(cpu.decodeInstruction(inst));
            assertTrue(cpu.getRegisters()[x] >= 0 &&
                    cpu.getRegisters()[x] <= 255);
        }
    }

    @Nested
    public class ClsTest {
        @Test
        public void clsDecodingTest() {
            char inst = 0x00e0;
            Opcode o = cpu.decodeInstruction(inst);
            assertEquals(Opcode.OpcodeType.CLS, o.getType());
        }
    }

    @Nested
    public class DrawTest {
        @Test
        public void drawDecodingTest() {
            char inst = 0xD01F;
            Opcode opcode = cpu.decodeInstruction(inst);
            assertEquals(Opcode.OpcodeType.DRW_VX_VY_NIBBLE, opcode.getType());
        }
    }

    @Test
    public void skpVxDecodingTest() {
        char inst = 0xE09E;
        Opcode opcode = cpu.decodeInstruction(inst);
        assertEquals(Opcode.OpcodeType.SKP_VX, opcode.getType());
    }

    @Test
    public void sknpVxDecodingTest() {
        char inst = 0xE0A1;
        Opcode opcode = cpu.decodeInstruction(inst);
        assertEquals(Opcode.OpcodeType.SKNP_VX, opcode.getType());
    }

    @Nested
    public class LdVxDtTest {
        @Test
        public void ldVxDtDecodingTest() {
            char inst = 0xF007;
            assertEquals(
                    Opcode.OpcodeType.LD_VX_DT,
                    cpu.decodeInstruction(inst).getType()
            );
        }

        @Test
        public void ldVxDtExecutionTest() {
            char x = 0xA;
            cpu.setDelayTimer((char)0x12);
            char inst = 0xFA07;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            assertEquals(0x0012, cpu.getRegisters()[x]);
        }
    }

    @Test
    public void ldVxKDecodingTest() {
        char inst = 0xF00A;
        Opcode opcode = cpu.decodeInstruction(inst);
        assertEquals(Opcode.OpcodeType.LD_VX_K, opcode.getType());
    }

    @Nested
    public class LdDtVxTest {
        @Test
        public void ldDtVxDecodingTest() {
            char inst = 0xF015;
            assertEquals(
                    Opcode.OpcodeType.LD_DT_VX,
                    cpu.decodeInstruction(inst).getType()
            );
        }

        @Test
        public void ldDtVxExecutionTest() {
            char x = 0xA;
            cpu.setDelayTimer((char)0x0012);
            cpu.getRegisters()[x] = 0XFA;
            char inst = 0xFA15;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            assertEquals(0xFA, cpu.getDelayTimer());
        }
    }

    @Nested
    public class LdStVxTest {
        @Test
        public void ldStVxDecodingTest() {
            char inst = 0xF018;
            assertEquals(
                    Opcode.OpcodeType.LD_ST_VX,
                    cpu.decodeInstruction(inst).getType()
            );
        }

        @Test
        public void ldStVxExecutionTest() {
            char x = 0xA;
            cpu.getRegisters()[x]=0xfa;
            cpu.setSoundTimer((char)0x0012);
            char inst = 0xFA18;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            assertEquals(0xfa, cpu.getSoundTimer());
        }
    }

    @Nested
    public class AddIVxTest {
        @Test
        public void addIVxDecodingTest() {
            char inst = 0xF01E;
            assertEquals(
                    Opcode.OpcodeType.ADD_I_VX,
                    cpu.decodeInstruction(inst).getType()
            );
        }

        @Test
        public void addIVxExecutionTest() {
            char x = 0xA;
            char vx = 0b1001_0110;
            char i = 0b1011_0110_1010;
            char expectedRes = 0b1100_0000_0000;

            char inst = 0XFA1E;

            cpu.getRegisters()[x] = vx;
            cpu.setIRegister(i);

            cpu.executeOpcode(cpu.decodeInstruction(inst));

            assertEquals(expectedRes, cpu.getIRegister());

        }

    }

    @Nested
    public class LdFVxTest {
        @Test
        public void ldFVxDecodingTest() {
            char i = 0xF029;
            assertEquals(Opcode.OpcodeType.LD_F_VX, cpu.decodeInstruction(i).getType());
        }

        @Test
        public void ldFVxExecutionTest() {
            char x = 0xB;
            cpu.getRegisters()[x] = 0x5;
            char inst = 0xFB29;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            char expected = 25;
            assertEquals(expected, cpu.getIRegister());
        }
    }

    @Nested
    public class LdBVxTest {
        @Test
        public void ldFBVxDecodingTest() {
            char i = 0xF033;
            assertEquals(Opcode.OpcodeType.LD_B_VX, cpu.decodeInstruction(i).getType());
        }

        @Test
        public void ldFVxExecutionTest() {
            char x = 0xB;
            cpu.getRegisters()[x] = 0xFF;
            cpu.setIRegister((char)500);
            char inst = 0xFB33;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            char expected = 25;
            assertEquals(2, memory.readByte(cpu.getIRegister()));
            assertEquals(5, memory.readByte(cpu.getIRegister() + 1));
            assertEquals(5, memory.readByte(cpu.getIRegister() + 2));
        }
    }

    @Nested
    public class LdIVxTest {
        @Test
        public void ldIVxDecodingTest() {
            char i = 0xF055;
            assertEquals(Opcode.OpcodeType.LD_I_VX, cpu.decodeInstruction(i).getType());
        }

        @Test
        public void ldFVxExecutionTest() {
            char x = 0x7;
            char[] values = {0x00FF, 0x00AB, 0X00BC, 0X00AF, 0X0012, 0X0083, 0X001F, 0X004C};
            for (int i = 0; i < values.length; i++) {
                cpu.getRegisters()[i] = values[i];
            }

            cpu.setIRegister((char)500);
            char inst = 0xFB55;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            for (int i = 0; i < values.length; i++) {
                assertEquals(values[i], memory.readByte(500 + i));
            }
        }
    }

    @Nested
    public class LdVxITest {
        @Test
        public void ldVxIDecodingTest() {
            char i = 0xF065;
            assertEquals(Opcode.OpcodeType.LD_VX_I, cpu.decodeInstruction(i).getType());
        }

        @Test
        public void ldVxIExecutionTest() {
            char x = 0x7;
            char[] values = {0x00FF, 0x00AB, 0X00BC, 0X00AF, 0X0012, 0X0083, 0X001F, 0X004C};
            for (int i = 0; i < values.length; i++) {
                cpu.getRegisters()[i] = values[i];
            }
            cpu.setIRegister((char)500);
            char inst = 0xFB55;
            cpu.executeOpcode(cpu.decodeInstruction(inst));

            for (int i = 0; i < values.length; i++) {
                cpu.getRegisters()[i] = 0;
            }

            char inst2 = 0xFB65;
            cpu.executeOpcode(cpu.decodeInstruction(inst2));

            for (int i = 0; i < values.length; i++) {
                assertEquals(values[i], cpu.getRegisters()[i]);
            }
        }
    }

}