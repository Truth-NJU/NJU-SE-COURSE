package instructions.store;

import instructions.base.NoOperandsInstruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.struct.JObject;


public class AASTORE extends NoOperandsInstruction {

    public void execute(StackFrame frame) {
        OperandStack operandStack = frame.getOperandStack();
        //所要赋的值
        JObject val = operandStack.popObjectRef();
        //数组索引
        int index = operandStack.popInt();
        //数组对象的引用
        JObject arrRef = operandStack.popObjectRef();

        if(arrRef==null){
            throw new NullPointerException();
        }
        //得到数组对象
        JObject[] refs = arrRef.getRefs();
        if(index<0 || index>=refs.length){
            throw new ArrayIndexOutOfBoundsException();
        }Store.checkIndex(arrRef.getArrayLen(), index);
        //将数组的 index 的元素进行赋值
        refs[index] = val;
    }
}