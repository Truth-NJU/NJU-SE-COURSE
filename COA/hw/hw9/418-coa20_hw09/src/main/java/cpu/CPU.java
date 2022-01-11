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
    //是测试用例使用到的接口，要求连续执行number条指令。本次作业中number恒定为1
    public int execInstr(long number) {
        // 执行过的指令的总长度
        int totalLen = 0;
        while (number > 0) {
            // TODO
           totalLen+=execInstr();
            number--;
        }
        return totalLen;
    }

    /**
     * execInstr a single instruction according to eip value
     */
    //会从eip寄存器中读取下一条指令的地址，传给CPU.decodeAndExecute(eip)
    private int execInstr() {
        String eip = CPU_State.eip.read();
        int len = decodeAndExecute(eip);
        return len;
    }


    //会从内存中取出下一条可执行的指令的opcode，创建对应的指令类并要求执行
    private int decodeAndExecute(String eip)  {
        int opcode = instrFetch(eip, 1);
        System.err.println("dxyyyds is"+opcode);
        //根据instr* 中的 *opcode* 查表获取指令类型
        Instruction instruction = InstrFactory.getInstr(opcode);
        assert instruction != null;

        //exec the target instruction
        //根据instr 中的 *opcode* 获取指令长度
        int len = instruction.exec(eip, opcode);
        return len;


    }

    /**
     * @param eip
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return
     */
    private int instrFetch(String eip, int length) {
        // TODO
        String cs=CPU_State.cs.read();
        String logicAddr=cs+eip;
        char[] opcode=mmu.read(logicAddr,length*8);

        return Integer.parseInt(transformer.binaryToInt(String.valueOf(opcode)));
    }

}

