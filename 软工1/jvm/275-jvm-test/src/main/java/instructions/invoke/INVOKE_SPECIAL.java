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


public class INVOKE_SPECIAL extends Index16Instruction {

    public void execute(StackFrame frame) throws ClassNotFoundException {
        //noinspection Duplicates
        //获取当前class
        JClass currentClz = frame.getMethod().getClazz();
        //获取当前class的运行时常量池并通过索引获取方法符号引用
        Constant methodRef = currentClz.getRuntimeConstantPool().getConstant(super.index);
        assert methodRef instanceof MethodRef;
        //和静态方法不同的是,要先加载方法所在的类
        JClass resolvedClass = ((MethodRef) methodRef).getResolvedClass();
        //解析所需要调用的方法
        Method method = ((MethodRef) methodRef).resolveMethodRef();
        if ((method.accessFlags & AccessFlags.ACC_NATIVE)!=0) return;

        //<init>方法必须在其对应的类进行声明,这里必须要验证类是否匹配
        if ("<init>".equals(method.getName()) && method.getClazz() != resolvedClass) {
            throw new NoSuchMethodError(method.getName());
        }

        if (method.isStatic()) {
            throw new IncompatibleClassChangeError(method.getName() + " in unstatic context");
        }




        JClass target;
        if (((currentClz.getAccessFlags()&AccessFlags.ACC_SUPER)!=0) && (!method.getName().equals("<init>"))) {
            JClass c=method.getClazz();
            if (((c.getAccessFlags()&AccessFlags.ACC_INTERFACE)==0)&& (c==currentClz.getSuperClass())){
                target=frame.getMethod().getClazz().getSuperClass();
            }else {
                target=method.getClazz();
            }
        }else {
            target=method.getClazz();
        }


        Method toInvoke = ((MethodRef) methodRef).resolveMethodRef(target);

        Slot[] args = copyArguments(frame, method);


        StackFrame newFrame = new StackFrame(frame.getThread(), toInvoke,
                toInvoke.getMaxStack(), toInvoke.getMaxLocal() + 1);
        Vars localVars = newFrame.getLocalVars();
        JObject thisRef = frame.getOperandStack().popObjectRef();

        Slot slot = new Slot();
        slot.setObject(thisRef);
        localVars.setSlot(0, slot);
        int argc = method.getArgc();
        for (int i = 1; i < args.length + 1; i++) {
            localVars.setSlot(i, args[argc - i]);
        }

        frame.getThread().pushFrame(newFrame);
    }

    private Slot[] copyArguments(StackFrame frame, Method method) {
        int argc = method.getArgc();
        Slot[] argv = new Slot[argc];
        for (int i = 0; i < argc; i++) {
            argv[i] = frame.getOperandStack().popSlot();
        }
        return argv;
    }

}
