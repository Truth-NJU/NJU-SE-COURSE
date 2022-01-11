package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import cpu.registers.EIP;
import transformer.Transformer;

public class Jz implements Instruction{
    MMU mmu= MMU.getMMU();
    Transformer t =new Transformer();
    @Override
    public int exec(String eip, int opcode) {
        //为1则跳转
        if(((EFlag)CPU_State.eflag).getZF()){
            String cs= CPU_State.cs.read();
            String logicAddr=cs+eip;
            char[] instr=mmu.read(logicAddr,16);
            CPU_State.eip.write(new ALU().add(eip,String.valueOf(instr).substring(8)));
            return 0;
        }else {
            ((EIP) CPU_State.eip).plus(16);
            return 16;
        }
    }
}
