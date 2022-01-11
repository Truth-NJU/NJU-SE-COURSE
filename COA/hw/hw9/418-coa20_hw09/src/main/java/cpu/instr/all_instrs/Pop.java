package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.registers.EIP;
import memory.Memory;

public class Pop implements Instruction {
    @Override
    public int exec(String eip, int opcode) {
        String esp=CPU_State.esp.read();
        String val= Memory.getMemory().topOfStack(esp);
        if(opcode==0x58){
            CPU_State.eax.write(val);
        }
        if(opcode==0x59){
            CPU_State.ecx.write(val);
        }
        if(opcode==0x5a){
            CPU_State.edx.write(val);
        }
        //new EIP().plus(8);
        ((EIP)CPU_State.eip).plus(8);
        return 8;
    }
}
