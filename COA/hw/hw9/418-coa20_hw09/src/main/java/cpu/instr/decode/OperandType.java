package cpu.instr.decode;

//The operand type for read and write
public enum OperandType {
    //立即数、寄存器、内存、
    OPR_IMM, OPR_REG, OPR_MEM, OPR_CREG, OPR_SREG
}
