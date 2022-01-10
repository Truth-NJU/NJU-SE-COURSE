package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class D2L extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        double value1=stack.popDouble();
        long value2= (long) value1;
        stack.pushLong(value2);
    }

}

