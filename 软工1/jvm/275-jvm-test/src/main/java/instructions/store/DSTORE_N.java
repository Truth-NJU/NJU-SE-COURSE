package instructions.store;

import runtime.OperandStack;
import runtime.StackFrame;

public class DSTORE_N extends STORE_N {
    public DSTORE_N(int index) {
        checkIndex(index);
        this.index = index;
    }

    public void execute(StackFrame frame) {
       /* OperandStack stack = frame.getOperandStack();
        double val=stack.popDouble();
        frame.getLocalVars().setDouble(index,val);
        frame.setLocalVars(frame.getLocalVars());*/
        Store.dstote(frame,index);

    }
}