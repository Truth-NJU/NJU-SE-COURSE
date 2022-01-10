package instructions.math;

import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.NoOperandsInstruction;

public class IDIV extends NoOperandsInstruction {


    public void execute(StackFrame frame) {
        //读取操作数栈
        OperandStack stack = frame.getOperandStack();
        //读取第二个操作数
        //（注意"栈"这个数据结构是后进先出的，所以第二个操作数先被读取）
        int val2 = stack.popInt();
        //读取第一个操作数
        int val1 = stack.popInt();
        int res=(int) (val1/val2);
        stack.pushInt(res);
    }

}
