package instructions.constant;

import instructions.base.NoOperandsInstruction;
import runtime.StackFrame;

public class LCONST_N extends NoOperandsInstruction {
    private long val;
    private static int[] valid = {0, 1};

    public LCONST_N(long val) {
        if (!(val >= valid[0] && val <= valid[valid.length - 1])) throw new IllegalArgumentException();
        this.val = val;
    }


    public void execute(StackFrame frame) {
        frame.getOperandStack().pushLong(val);
    }

    @Override
    public String toString() {
       return null;
    }
}
