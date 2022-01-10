package instructions.load;

import runtime.StackFrame;

public class FLOAD_N extends LOAD_N {
    public FLOAD_N(int index) {
        checkIndex(index);
        this.index = index;
    }


    public void execute(StackFrame frame) {
        /*float value=frame.getLocalVars().getFloat(this.index);
        frame.getOperandStack().pushFloat(value);*/
        Load.fload(frame,index);
    }

}
