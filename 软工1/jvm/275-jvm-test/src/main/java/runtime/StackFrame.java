package runtime;

import com.njuse.jvmfinal.memory.jclass.Method;


public class StackFrame {
    //public StackFrame lower;  //当前帧的前一帧的引用;相当于单向链表的前一个指针
    private OperandStack operandStack; //操作数栈的引用;
    private Vars localVars; //局部变量表的引用;
    private JThread thread;  //当前栈帧所在的线程;
    private int nextPC;  //frame中并不改变PC的值,其PC值是由ByteReader读取字节码不断改变的
    private Method method;

    public StackFrame(JThread thread, Method method, int maxStackSize, int maxVarSize) {
        this.thread = thread;
        this.method = method;
        this.operandStack = new OperandStack(maxStackSize);
        this.localVars = new Vars(maxVarSize);
    }

    public OperandStack getOperandStack() {
        return this.operandStack;
    }

    public Vars getLocalVars() {
        return this.localVars;
    }

    public JThread getThread() {
        return this.thread;
    }

    public int getNextPC() {
        return this.nextPC;
    }

    public void setNextPC(int nextPC) {
        this.nextPC = nextPC;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setLocalVars(Vars localVars) {
        this.localVars = localVars;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
