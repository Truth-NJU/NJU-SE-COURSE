package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.registers.EIP;
import memory.Memory;

public class Push implements Instruction {
    Memory memory=Memory.getMemory();

    @Override
    public int exec(String eip, int opcode) {
        String reg = CPU_State.ebx.read();
        String esp=CPU_State.esp.read();
        memory.pushStack(esp,reg);
        //new EIP().plus(8);
        ((EIP)CPU_State.eip).plus(8);
        return 8;
    }


}
