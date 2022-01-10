package instructions.comparsion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class LCMP extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        long val2 = stack.popLong();
        long val1 = stack.popLong();
        if (val1 > val2) {
            stack.pushInt(1);
        } else if (val1 == val2) {
            stack.pushInt(0);
        } else {
            stack.pushInt(-1);
        }
    }

}
