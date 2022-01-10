package runtime;


public class JThread {
    private ThreadStack stack;
    private int pc;         //该PC也不是自己修改的,而是由外部传入供当前线程所持有的;


    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }
    public JThread() {
        stack = new ThreadStack();
    }

    public void pushFrame(StackFrame frame) {
        stack.pushFrame(frame);
    }

    public void popFrame() {
        stack.popFrame();
    }

    public StackFrame getTopFrame() {
        return stack.getTopFrame();
    }
}
