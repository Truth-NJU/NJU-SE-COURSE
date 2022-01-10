package instructions.math;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class LADD extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        //读取操作数栈
        OperandStack stack = frame.getOperandStack();
        //读取第二个操作数
        //（注意"栈"这个数据结构是后进先出的，所以第二个操作数先被读取）
        long val2 = stack.popLong();
        //读取第一个操作数
        long val1=stack.popLong();
        //计算结果
        long res = val1 + val2;
        //将结果放入操作数栈
        stack.pushLong(res);
    }
}
