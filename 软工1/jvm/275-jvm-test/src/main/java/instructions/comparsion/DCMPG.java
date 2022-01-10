package instructions.comparsion;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

import java.math.BigDecimal;

public class DCMPG extends NoOperandsInstruction {

    @Override
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        double value2 = stack.popDouble();
        double value1 = stack.popDouble();
       if(Double.isNaN(value1)||Double.isNaN(value2)){
           stack.pushInt(1);
       }else {
           stack.pushInt(Double.compare(value1,value2));
       }
    }

}
