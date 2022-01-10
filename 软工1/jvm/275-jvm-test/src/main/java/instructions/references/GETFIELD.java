package instructions.references;

import com.njuse.jvmfinal.memory.jclass.Field;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.FieldRef;
import instructions.base.Index16Instruction;
import runtime.OperandStack;
import runtime.StackFrame;
import runtime.Vars;
import runtime.struct.JObject;
import runtime.struct.NonArrayObject;

import java.nio.ByteBuffer;

public class GETFIELD extends Index16Instruction {

    public void execute(StackFrame frame) {

        //获取符号引用
        RuntimeConstantPool runtimeConstantPool = frame.getMethod().getClazz().getRuntimeConstantPool();
        FieldRef fieldRef = (FieldRef) runtimeConstantPool.getConstant(index);
        Field field;
        try {
            //将字段符号引用转换为直接引用
            field = fieldRef.getResolvedFieldRef();

            if (field.isStatic()) {
                throw new IncompatibleClassChangeError();
            }
            String descriptor = field.getDescriptor();
            int slotID = field.getSlotID();
            OperandStack stack = frame.getOperandStack();
            JObject ref=stack.popObjectRef();

            if(ref==null){
                throw new NullPointerException();
            }

            Vars staticVars = ((NonArrayObject) ref).getFields();
            switch (descriptor.charAt(0)) {
                case 'Z':
                case 'B':
                case 'C':
                case 'S':
                case 'I':
                    stack.pushInt(staticVars.getInt(slotID));
                    break;
                case 'F':
                    stack.pushFloat(staticVars.getFloat(slotID));
                    break;
                case 'J':
                    stack.pushLong(staticVars.getLong(slotID));
                    break;
                case 'D':
                    stack.pushDouble(staticVars.getDouble(slotID));
                    break;
                case 'L':
                case '[':
                    stack.pushObjectRef(staticVars.getObjectRef(slotID));
                    break;
                default:
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
