package instructions.math;

import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;

public class INEG extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int val = stack.popInt();
        stack.pushInt(0-val);
    }


}
