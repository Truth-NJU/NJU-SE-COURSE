package instructions.math;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class IXOR extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        int val2 = operandStack.popInt();
        int val1 = operandStack.popInt();
        int res = val1 ^ val2;
        operandStack.pushInt(res);
    }

}
