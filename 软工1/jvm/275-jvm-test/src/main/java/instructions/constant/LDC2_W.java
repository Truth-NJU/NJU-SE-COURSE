package instructions.constant;

import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.DoubleWrapper;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.wrapper.LongWrapper;
import instructions.base.Index16Instruction;
import runtime.OperandStack;
import runtime.StackFrame;

//LDC2_W 和 LDC 的区别是，其获取常量池的常量类型为 Long 和 Double，都是 16bit 宽的
public class LDC2_W extends Index16Instruction {

    public LDC2_W(){

    }
    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        JClass clazz = frame.getMethod().getClazz();
        Constant constant = clazz.getRuntimeConstantPool().getConstant(index);
        if (constant instanceof LongWrapper) {
            // 如果这个元素是LongWrapper
            operandStack.pushLong(((LongWrapper) constant).getValue());
        }
        else if (constant instanceof DoubleWrapper) {
           operandStack.pushDouble(((DoubleWrapper) constant).getValue());
        }

    }

}
