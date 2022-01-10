package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class F2L extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        float value1 = stack.popInt();
        long value2 = (long) value1;
        stack.pushLong(value2);
    }

}