package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class D2I extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        double value1=stack.popDouble();
        int value2= (int) value1;
        stack.pushInt(value2);
    }

}

