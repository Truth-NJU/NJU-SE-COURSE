package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class F2D extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        float value1 = stack.popInt();
        double value2 = (double) value1;
        stack.pushDouble(value2);
    }


}
