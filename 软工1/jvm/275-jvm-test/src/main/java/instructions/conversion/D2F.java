package instructions.conversion;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class D2F extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        double value1=stack.popDouble();
        float value2= (float) value1;
        stack.pushFloat(value2);
    }


}

