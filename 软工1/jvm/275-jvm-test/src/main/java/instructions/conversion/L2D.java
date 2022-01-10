package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class L2D extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        long value1=stack.popLong();
        double value2= (double) value1;
        stack.pushDouble(value2);
    }

}

