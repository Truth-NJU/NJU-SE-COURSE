package instructions.load;

import instructions.base.NoOperandsInstruction;
import jdk.nashorn.internal.scripts.JO;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;

public class IALOAD extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        //数组元素的索引值
        int index = operandStack.popInt();
        //数组对象的引用
        JObject arrayRef = operandStack.popObjectRef();
        if(arrayRef==null){
            throw new NullPointerException();
        }
        //得到数组对象
        int[] array=arrayRef.getInts();
        //将数组的 index 的值压栈
        if(index<0 || index>=array.length){
            throw new ArrayIndexOutOfBoundsException();
        }
        operandStack.pushInt(array[index]);

    }
}
