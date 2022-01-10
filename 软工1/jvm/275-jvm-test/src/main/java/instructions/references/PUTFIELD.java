package instructions.references;

import com.njuse.jvmfinal.memory.jclass.Field;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.FieldRef;
import runtime.OperandStack;
import runtime.StackFrame;
import instructions.base.Index16Instruction;
import runtime.struct.JObject;
import runtime.struct.NonArrayObject;

import java.nio.ByteBuffer;

public class PUTFIELD extends Index16Instruction {

    public void execute(StackFrame frame) {
        RuntimeConstantPool runtimeConstantPool = frame.getMethod().getClazz().getRuntimeConstantPool();
        //首先获取到fieldRef引用;
        FieldRef fieldRef = (FieldRef) runtimeConstantPool.getConstant(index);
        try {
            //根据引用获取到字段;
            Field field=fieldRef.getResolvedFieldRef();
            JClass targetClazz = field.getClazz();

            if (field.isStatic()) {
                throw new IncompatibleClassChangeError("should not call a static field by an instance");
            }

            if (field.isFinal()) {
                if (frame.getMethod().getClazz() != targetClazz || "<clinit>".equals(frame.getMethod().getName())) {
                    throw new IllegalAccessError(field.getName()+" can't be assigned out of instance");
                }
            }
            String descriptor = field.getDescriptor();
            int slotID = field.getSlotID();
            OperandStack stack = frame.getOperandStack();
            JObject ref;
            switch (descriptor.charAt(0)) {
                case 'Z':
                case 'B':
                case 'C':
                case 'S':
                case 'I':
                    int intVal=stack.popInt();
                    ref =stack.popObjectRef();
                    if(ref==null){
                        throw new NullPointerException("11");
                    }
                    ((NonArrayObject) ref).getFields().setInt(slotID,intVal);
                    break;
                case 'F':
                    float floatVal=stack.popFloat();
                    ref =stack.popObjectRef();
                    if(ref==null){
                        throw new NullPointerException("11");
                    }
                    ((NonArrayObject) ref).getFields().setFloat(slotID,floatVal);
                    break;
                case 'J':
                    long longVal=stack.popLong();
                    ref =stack.popObjectRef();
                    if(ref==null){
                        throw new NullPointerException("11");
                    }
                    ((NonArrayObject) ref).getFields().setLong(slotID,longVal);
                    break;
                case 'D':
                    double doubleVal=stack.popDouble();
                    ref =stack.popObjectRef();
                    if(ref==null){
                        throw new NullPointerException("11");
                    }
                    ((NonArrayObject) ref).getFields().setDouble(slotID,doubleVal);
                    break;
                case 'L':
                case '[':
                    JObject reference=stack.popObjectRef();
                    ref =stack.popObjectRef();
                    if(ref==null){
                        throw new NullPointerException("11");
                    }
                    ((NonArrayObject) ref).getFields().setObjectRef(slotID,reference);
                    break;
                default:
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
