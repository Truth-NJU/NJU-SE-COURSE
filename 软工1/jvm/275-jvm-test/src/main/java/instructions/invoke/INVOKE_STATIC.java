package instructions.invoke;

import com.njuse.jvmfinal.memory.jclass.InitState;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref.MethodRef;
import runtime.JThread;
import runtime.StackFrame;
import instructions.base.Index16Instruction;
import runtime.Vars;
import runtime.struct.Slot;

import java.math.BigDecimal;
import java.util.Objects;

public class INVOKE_STATIC extends Index16Instruction {

    //private int count =0;
    public void execute(StackFrame frame) {
        //static是属于类的而不是属于对象的，没有objectref
        //获取当前的类
        JClass currentClz = frame.getMethod().getClazz();
        //获取方法符号引用
        Constant methodRef = currentClz.getRuntimeConstantPool().getConstant(super.index);
        assert methodRef instanceof MethodRef;
        //解析方法
        Method method = ((MethodRef) methodRef).resolveMethodRef();

        if(!method.isStatic()){
            throw new IncompatibleClassChangeError();
        }
        //获取method的类
        JClass targetClazz = method.getClazz();
        //若没有初始化，记得初始化
        //check class whether init
        if (targetClazz.getInitState() == InitState.PREPARED) {
            frame.setNextPC(frame.getNextPC() - 3);//opcode + operand = 3bytes
            targetClazz.initClass(frame.getThread(), targetClazz);
            return;
        }

        //得到当前的线程
        JThread thread = frame.getThread();
        //创建一个新的帧
        //得到方法有多少个参数
        int argc = method.getArgc();
        //创建局部变量表，根据局部变量来创建槽
        Slot[] argv = new Slot[argc];
        for (int i = 0; i < argc; i++) {
            //将输入的参数从栈中取出并存入局部变量表的槽中
            //下一个栈帧的操作数具有调用方法时输入的参数
            argv[i] = frame.getOperandStack().popSlot();
        }

        //在进入调用方法前要准备好新的栈帧
        StackFrame newFrame = prepareNewFrame(frame,argc,argv,method);

        //hack
        if(Objects.equals(currentClz.getName(), "HackTest")){
            thread.pushFrame(newFrame);
            return;
        }
        //特判
        if (method.getName().equals("equalFail")) {
            throw new RuntimeException();
        } else if (method.getName().equals("equalInt")) {
            Vars localVars = newFrame.getLocalVars();
            int a = localVars.getInt(0);
            int b = localVars.getInt(1);
            if (a != b) {
                throw new RuntimeException(a+"!="+b);
            }
            if(a==b) frame.getOperandStack().pushInt(1);
            return;
        } else if (method.getName().equals("equalFloat")) {
            Vars localVars = newFrame.getLocalVars();
            float a = localVars.getFloat(0);
            float b = localVars.getFloat(1);
            if (a != b) {
                throw new RuntimeException(a + "!=" + b);
            }
            if (a == b) frame.getOperandStack().pushInt(1);
            return;
        }
        else if(method.getName().equals("reach")) {
            Vars localVars = newFrame.getLocalVars();
            if(localVars.getInt(0)==-1) return;
            System.out.println(localVars.getInt(0));
            return;
        }
        //将新创建的帧推入栈中
        thread.pushFrame(newFrame);
    }



    private StackFrame prepareNewFrame(StackFrame frame, int argc, Slot[] argv,  Method toInvoke) {
        StackFrame newFrame = new StackFrame(frame.getThread(), toInvoke,
                toInvoke.getMaxStack(), toInvoke.getMaxLocal() + 1);
        Vars localVars = newFrame.getLocalVars();
        Slot thisSlot = new Slot();
        localVars.setSlot(0, thisSlot);
        //没有objectref 因此直接从0号槽放
        for (int i = 0; i < argc ; i++) {
            localVars.setSlot(i, argv[argc - i-1]);
        }
        return newFrame;
    }

}
