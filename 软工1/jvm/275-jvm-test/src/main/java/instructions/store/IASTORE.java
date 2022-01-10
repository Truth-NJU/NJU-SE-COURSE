package instructions.store;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

public class IASTORE extends NoOperandsInstruction {
    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        //所要赋的值
        int val = operandStack.popInt();
        //数组索引
        int index = operandStack.popInt();
        //数组对象的引用
        JObject arrRef = operandStack.popObjectRef();
        //jvm规范
        if(arrRef==null){
            throw new NullPointerException();
        }
        //得到数组对象
        int[] array = arrRef.getInts();
        //jvm规范
        if(index<0 || index>=array.length){
            throw new ArrayIndexOutOfBoundsException();
        }
        //将数组的 index 的元素进行赋值
        array[index]=val;
    }

}
