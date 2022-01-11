package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EIP;

public class Sbb implements Instruction {
    MMU mmu = MMU.getMMU();

    @Override
    public int exec(String eip, int opcode) {
        String cs = CPU_State.cs.read();
        String logicAddr = cs + eip;
        char[] instr = mmu.read(logicAddr, 40);
        String imm = String.valueOf(instr).substring(8);
        String reg = CPU_State.eax.read();
        String a = new ALU().sub(imm, reg);
        String res = new ALU().sub("00000000000000000000000000000001",a);
        CPU_State.eax.write(res);
        //new EIP().plus(40);
        ((EIP)CPU_State.eip).plus(40);
        return 40;
    }
}
