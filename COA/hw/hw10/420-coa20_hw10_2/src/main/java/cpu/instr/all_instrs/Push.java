package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.registers.EIP;
import memory.Memory;
import transformer.Transformer;

public class Push implements Instruction {
    Memory memory=Memory.getMemory();
    Transformer t=new Transformer();

    @Override
    public int exec(String eip, int opcode) {
        //先减四再做操作
        String reg = CPU_State.ebx.read();
        String esp=CPU_State.esp.read();
        String newEsp=new ALU().sub("00000000000000000000000000000100",esp);
        memory.pushStack(newEsp,reg);
        CPU_State.esp.write(newEsp);
        ((EIP)CPU_State.eip).plus(8);
        return 8;
    }


}
