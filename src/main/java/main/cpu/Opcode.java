package main.cpu;

/**
 * An opcode represents a single instruction
 */
public class Opcode {
    private OpcodeType type;
    private char instruction;

    public Opcode(OpcodeType type, char instruction) {
        this.type = type;
        this.instruction = instruction;
    }

    public enum OpcodeType {
        CLS, RET, JP_ADDR, CALL_ADDR, SE_VX_BYTE, SNE_VX_BYTE, SE_VX_VY, LD_VX_BYTE, ADD_VX_BYTE,
        LD_VX_VY, OR_VX_VY, AND_VX_VY, XOR_VX_VY, ADD_VX_VY, SUB_VX_VY, SHR_VX_VY, SUBN_VX_VY,
        SHL_VX_VY, SNE_VX_VY, LD_I_ADDR, JP_V0_ADDR, RND_VX_BYTE, DRW_VX_VY_NIBBLE, SKP_VX,
        SKNP_VX, LD_VX_DT, LD_VX_K, LD_DT_VX, LD_ST_VX, ADD_I_VX, LD_F_VX, LD_B_VX, LD_I_VX, LD_VX_I,
        UNSUPPORTED
    }

    public OpcodeType getType() {
        return type;
    }

    public void setType(OpcodeType type) {
        this.type = type;
    }

    public char getInstruction() {
        return instruction;
    }

    public void setInstruction(char instruction) {
        this.instruction = instruction;
    }
}

