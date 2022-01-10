package execution;

import com.njuse.jvmfinal.memory.jclass.Method;
import instructions.base.Instruction;
import runtime.JThread;
import runtime.StackFrame;

import java.nio.ByteBuffer;

/*jvm大致逻辑：
1、计算 pc 值（默认 pc++，取下一条指令，否则就是跳转指令，对应 pc + offset ）；
2、根据 pc 寄存器的指示位置，从字节码中读取出操作码；
3、如果该操作码存在操作数，那么继续从字节码中读取操作数；
4、执行操作码所定义的操作；
5、如果字节码还未读取完，继续步骤 1；
 */

public class Interpreter {
    private static ByteBuffer codeReader;
    private static int index = 1;

    public static void interpret(JThread thread) {
        initCodeReader(thread);
        loop(thread);

    }

    private static void initCodeReader(JThread thread) {
        byte[] code = thread.getTopFrame().getMethod().getCode();
        codeReader = ByteBuffer.wrap(code);
        int nextPC = thread.getTopFrame().getNextPC();
        codeReader.position(nextPC);
    }

    private static void loop(JThread thread) {
        try {
            index = 1;
            while (true) {
                //读取ThreadStack的栈顶
                StackFrame oriTop = thread.getTopFrame();
                //获取栈顶方法
                Method method = oriTop.getMethod();
                if (!method.isParsed()) {
                    method.parseCode();
                }
                //设置PC
                codeReader.position(oriTop.getNextPC());
                //取指令
                int opcode = codeReader.get() & 0xff;
                //译码
                Instruction instruction = Decoder.decode(opcode);
                //System.err.println("指令" + index + ":" + Integer.toHexString(opcode) + " " + instruction.getName());
                index++;
                //读取指令的剩余部分
                instruction.fetchOperands(codeReader);
                //更新PC
                int nextPC = codeReader.position();
                oriTop.setNextPC(nextPC);
                //执行指令
                instruction.execute(oriTop);
                //检查是否需要切换栈帧
                StackFrame newTop = thread.getTopFrame();
                if (newTop == null) {
                    //如果新栈帧为空，则意味着main函数已经返回，这个程序已经执行完了
                    return;
                }

                if (oriTop != newTop) {
                    //切换栈帧
                    //System.err.println("新方法： "+thread.getTopFrame().getMethod().getClazz().getName()+" "+thread.getTopFrame().getMethod().getName());
                    initCodeReader(thread);
                }
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
