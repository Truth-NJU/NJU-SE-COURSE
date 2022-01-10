package instructions.constant;

import instructions.base.NoOperandsInstruction;
import runtime.StackFrame;

public class FCONST_N extends NoOperandsInstruction {
    private float val;
    private static int[] valid = {0, 1, 2};

    public FCONST_N(float val) {
        if (!(val >= valid[0] && val <= valid[valid.length - 1])) throw new IllegalArgumentException();
        this.val = val;
    }


    public void execute(StackFrame frame) {
        frame.getOperandStack().pushFloat(val);
    }

    @Override
    public String toString() {
        /*String suffix = (val == -1) ? "M1" : "" + val;
        String simpleName = this.getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - 1) + suffix;*/
        return null;
    }

}
