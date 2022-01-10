package runtime;

import runtime.StackFrame;

import java.util.EmptyStackException;
import java.util.Stack;

public class ThreadStack {
    private static int maxSize;  //虚拟机栈中所包含栈帧的最大容量
    private Stack<StackFrame> stack;
    private Stack<Boolean> frameState;//true present frame is new added
    private int currentSize;

    static {
        maxSize = 64 * 1024;//use linux x86_64 default value 256KB
    }

    public ThreadStack() {
        stack = new Stack<>();
        frameState = new Stack<>();
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public void pushFrame(StackFrame frame) {
        if (currentSize >= maxSize) {
            throw new StackOverflowError();
        }
        stack.push(frame);
        frameState.push(true);
        currentSize++;
    }

    public void popFrame() {
        if (currentSize == 0) {
            throw new EmptyStackException();
        }
        stack.pop();
        frameState.pop();
        currentSize--;
    }

    public StackFrame getTopFrame() {
        if (currentSize == 0) return null;
        return stack.lastElement();
    }

    public Stack<StackFrame> getStack() {
        return this.stack;
    }

    public Stack<Boolean> getFrameState() {
        return this.frameState;
    }
}

