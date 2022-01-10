package instructions.constant;

import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class ICONST_N extends NoOperandsInstruction {
    private int val;
    private static int[] valid = {-1, 0, 1, 2, 3, 4, 5};

    public ICONST_N(int val) {
        if (!(val >= valid[0] && val <= valid[valid.length - 1])) throw new IllegalArgumentException();
        this.val = val;
    }


    public void execute(StackFrame frame) {
        frame.getOperandStack().pushInt(this.val);
    }

    @Override
    public String toString() {
        return null;
    }

}
