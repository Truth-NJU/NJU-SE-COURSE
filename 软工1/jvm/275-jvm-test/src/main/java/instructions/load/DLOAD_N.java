package instructions.load;

import runtime.StackFrame;

public class DLOAD_N extends LOAD_N {
    public DLOAD_N(int index) {
        checkIndex(index);
        this.index = index;
    }


    public void execute(StackFrame frame) {
        /*double value=frame.getLocalVars().getDouble(this.index);
        frame.getOperandStack().pushDouble(value);*/
        Load.dload(frame,index);
    }

}
