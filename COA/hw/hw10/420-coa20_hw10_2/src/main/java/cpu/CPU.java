package cpu;

import cpu.instr.all_instrs.InstrFactory;
import cpu.instr.all_instrs.Instruction;
import transformer.Transformer;

public class CPU {

    Transformer transformer = new Transformer();
    MMU mmu = MMU.getMMU();


    /**
     * execInstr specific numbers of instructions
     *
     * @param number numbers of instructions
     */
    public int execInstr(long number) {
        // 执行过的指令的总长度
        int totalLen = 0;
        while (number > 0) {
            totalLen+=execInstr();
            number--;
        }
        return totalLen;
    }

    /**
     * execInstr a single instruction according to eip value
     */
    private int execInstr() {
        String eip = CPU_State.eip.read();
        int len = decodeAndExecute(eip);
        return len;
    }

    private int decodeAndExecute(String eip) {
        int opcode = instrFetch(eip, 1);
        Instruction instruction = InstrFactory.getInstr(opcode);
        assert instruction != null;

        //exec the target instruction
        int len = instruction.exec(eip, opcode);
        return len;


    }

    /**
     * @param eip
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return
     */
    private int instrFetch(String eip, int length) {
        // TODO X   FINISHED √
        String cs=CPU_State.cs.read();
        String logicAddr=cs+eip;
        char[] opcode=mmu.read(logicAddr,length*8);

        return Integer.parseInt(transformer.binaryToInt(String.valueOf(opcode)));
    }

    public void execUntilHlt(){
        // TODO ICC
        String cs=CPU_State.cs.read();
        String eip=CPU_State.eip.read();
        String logicAddr=cs+eip;
        char[] opcode=mmu.read(logicAddr,8);
        int op=Integer.parseInt(transformer.binaryToInt(String.valueOf(opcode)));
        while(op!=0xf4){
            execInstr();
            String cs1=CPU_State.cs.read();
            String eip1=CPU_State.eip.read();
            String logicAddr1=cs1+eip1;
            char[] opcode1=mmu.read(logicAddr1,8);
            op=Integer.parseInt(transformer.binaryToInt(String.valueOf(opcode1)));
        }
    }

}

