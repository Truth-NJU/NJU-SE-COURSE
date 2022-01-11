package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.registers.EIP;

public class Hlt implements Instruction {
    @Override
    public int exec(String eip, int opcode) {
        ((EIP) CPU_State.eip).plus(8);
        return 8;
    }
}
