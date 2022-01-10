package instructions.constant;

import instructions.base.NoOperandsInstruction;
import runtime.StackFrame;
//nop指令是最简单的一条指令，因为它什么也不做
public class NOP extends NoOperandsInstruction {
    public NOP() {
    }

    public void execute(StackFrame frame) {
    }

}
