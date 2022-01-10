package instructions.invoke;

import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.InterfaceMethodRef;

import runtime.StackFrame;
import instructions.base.Index16Instruction;
import runtime.Vars;
import runtime.struct.JObject;
import runtime.struct.Slot;

import java.nio.ByteBuffer;


public class INVOKE_INTERFACE extends Index16Instruction {

    public void fetchOperands(ByteBuffer reader) {
        //branchbyte1和branchbyte2是两个byte类型的数据，可以通过一个getShort来读取
        super.index=(int)reader.getShort()& 0xFFFF;
        int m=reader.getShort();
    }


    public void execute(StackFrame frame) {
        try {
            //获取当前类
            JClass currentClz = frame.getMethod().getClazz();
            //获取接口方法符号引用
            Constant interfaceMethodRef = currentClz.getRuntimeConstantPool().getConstant(index);
            assert interfaceMethodRef instanceof InterfaceMethodRef;
            //泛泛的，获得interfaceMethod
            //解析接口方法，将接口方法的符号引用转为直接引用
            Method interfaceMethod = ((InterfaceMethodRef) interfaceMethodRef).resolveInterfaceMethodRef();

            //copy arguments
            int argc = interfaceMethod.getArgc();
            Slot[] argv = new Slot[argc];
            for (int i = 0; i < argc; i++) {
                argv[i] = frame.getOperandStack().popSlot();
            }
            //lookup virtual function
            JObject objectRef = frame.getOperandStack().popObjectRef();
            JClass clazz = objectRef.getClazz();
            //获得对象的具体的方法
            Method toInvoke = ((InterfaceMethodRef) interfaceMethodRef).resolveInterfaceMethodRef(clazz);

            StackFrame newFrame = prepareNewFrame(frame, argc, argv, objectRef, toInvoke);

            frame.getThread().pushFrame(newFrame);

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    private StackFrame prepareNewFrame(StackFrame frame, int argc, Slot[] argv, JObject objectRef, Method toInvoke) {
        StackFrame newFrame = new StackFrame(frame.getThread(), toInvoke,
                toInvoke.getMaxStack(), toInvoke.getMaxLocal()+1);
        Vars localVars = newFrame.getLocalVars();
        Slot thisSlot = new Slot();
        thisSlot.setObject(objectRef);
        localVars.setSlot(0, thisSlot);
        for (int i = 1; i < argc + 1; i++) {
            localVars.setSlot(i, argv[argc - i]);
        }
        return newFrame;
    }

}
