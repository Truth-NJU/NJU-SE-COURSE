package instructions.constant;

import instructions.base.NoOperandsInstruction;
import runtime.StackFrame;


public class DCONST_N extends NoOperandsInstruction {
    private double val;
    private static double[] valid = {0.0,1.0};

    public DCONST_N(double val) {
        if (!(val >= valid[0] && val <= valid[valid.length - 1])) throw new IllegalArgumentException();
        this.val = val;
    }


    public void execute(StackFrame frame) {
        frame.getOperandStack().pushDouble(val);
    }

    @Override
    public String toString() {
       return null;
    }

}
