package instructions.others;

import instructions.base.NoOperandsInstruction;
import runtime.StackFrame;

public class POP extends NoOperandsInstruction {
    public POP() {
    }

    public void execute(StackFrame frame) {
        frame.getOperandStack().popSlot();
    }

}
