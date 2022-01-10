package instructions.store;

import runtime.StackFrame;
import runtime.OperandStack;

public class ISTORE_N extends STORE_N {
    public ISTORE_N(int index) {
        checkIndex(index);
        this.index = index;
    }

    public void execute(StackFrame frame) {
       /* OperandStack stack = frame.getOperandStack();
        int val=stack.popInt();
        frame.getLocalVars().setInt(index,val);
        frame.setLocalVars(frame.getLocalVars());*/
        Store.istore(frame,index);

    }

}