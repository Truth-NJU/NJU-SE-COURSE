package instructions.math;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class DADD extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        //读取操作数栈
        OperandStack stack = frame.getOperandStack();
        //读取第二个操作数
        //（注意"栈"这个数据结构是后进先出的，所以第二个操作数先被读取）
        double val2 = stack.popDouble();
        //读取第一个操作数
        double val1 = stack.popDouble();
        //计算结果
        double res = val1 + val2;
        //将结果放入操作数栈
        stack.pushDouble(res);
    }
}

