package instructions.references;

import com.njuse.jvmfinal.memory.jclass.InitState;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.ClassRef;
import runtime.StackFrame;
import instructions.base.Index16Instruction;
import runtime.struct.JObject;

public class NEW extends Index16Instruction {

    public void execute(StackFrame frame) {
        // 获取运行时常量池、 通过索引从常量池中获取类符号引用
        ClassRef classRef =(ClassRef) frame.getMethod().getClazz().getRuntimeConstantPool().getConstant(index);
        try {
            //解析类
            JClass clazz=classRef.getResolvedClass();
            //验证初始化状态
            if (clazz.getInitState()== InitState.PREPARED) {
                frame.setNextPC(frame.getNextPC()-3);
                clazz.initClass(frame.getThread(), clazz);
                return;
            }
            //验证
            if (clazz.isInterface() || clazz.isAbstract()) {
                throw new InstantiationException();
            }
            // 创建对象引用
            JObject ref=clazz.newObject();
            // 将对象引用入栈
            frame.getOperandStack().pushObjectRef(ref);
        } catch (ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }

    }

}
