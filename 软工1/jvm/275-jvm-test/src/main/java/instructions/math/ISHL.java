package instructions.math;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;

public class ISHL extends NoOperandsInstruction {
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int val2 = stack.popInt();  //要移动多少bit
        int val1 = stack.popInt();  //要进行位移操作的变量
        int s = val2 & 0x1f; //int变量只有32位，所以只取val2的后5个比特就足够表示位移位数了,位移操作符右侧必须是无符号整数，所以需要对val2进行类型转换

        //但是Java中对于大数左移超出后,也会变成负数,所以这里不做额外处理了
        int res = val1 << s;

        stack.pushInt(res);

    }

}
