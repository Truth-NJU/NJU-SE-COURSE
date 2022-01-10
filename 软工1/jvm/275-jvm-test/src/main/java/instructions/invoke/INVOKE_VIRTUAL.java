package instructions.invoke;

import com.njuse.jvmfinal.memory.jclass.AccessFlags;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.MethodRef;
import runtime.StackFrame;
import instructions.base.Index16Instruction;
import runtime.Vars;
import runtime.struct.JObject;
import runtime.struct.Slot;

public class INVOKE_VIRTUAL extends Index16Instruction {

    //找到相应的方法，变成新的栈帧压到线程栈中
    public void execute(StackFrame frame) {
        try {
            JClass currentClz = frame.getMethod().getClazz();
            Constant methodRef = currentClz.getRuntimeConstantPool().getConstant(super.index);
            assert methodRef instanceof MethodRef;
            Method method = ((MethodRef) methodRef).resolveMethodRef();
            if ((method.accessFlags & AccessFlags.ACC_NATIVE)!=0) return;
            //copy arguments（获得参数）
            //得到方法有多少个参数
            int argc = method.getArgc();
            //创建局部变量表，根据局部变量来创建槽
            Slot[] argv = new Slot[argc];
            for (int i = 0; i < argc; i++) {
                //将输入的参数从栈中取出并存入局部变量表的槽中
                //下一个栈帧的操作数具有调用方法时输入的参数
                argv[i] = frame.getOperandStack().popSlot();
            }
            //lookup virtual function（找调用的方法<原函数>）
            //获得引用
            JObject objectRef = frame.getOperandStack().popObjectRef();
            //获得类
            JClass clazz = objectRef.getClazz();
            //在class中解析获得需要调用的方法
            Method toInvoke = ((MethodRef) methodRef).resolveMethodRef(clazz);
            //在进入调用方法前要准备好新的栈帧
            StackFrame newFrame = prepareNewFrame(frame, argc, argv, objectRef, toInvoke);
            //将新的栈帧压入线程栈
            frame.getThread().pushFrame(newFrame);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private StackFrame prepareNewFrame(StackFrame frame, int argc, Slot[] argv, JObject objectRef, Method toInvoke) {
        StackFrame newFrame = new StackFrame(frame.getThread(), toInvoke,
                toInvoke.getMaxStack(), toInvoke.getMaxLocal() + 1);
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
