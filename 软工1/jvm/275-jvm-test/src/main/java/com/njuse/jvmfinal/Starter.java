package com.njuse.jvmfinal;


import classloader.ClassLoader;
import classloader.classfilereader.ClassFileReader;
import com.njuse.jvmfinal.memory.jclass.InitState;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import execution.Interpreter;
import runtime.JThread;
import runtime.StackFrame;

import java.io.File;

public class Starter {

    public static final String PATH_SEPARATOR = File.pathSeparator;
    public static final String FILE_SEPARATOR = File.separator;

    public static void main(String[] args) {
    }

    /**
     * ⚠️警告：不要改动这个方法签名，这是和测试用例的唯一接口
     */
    public static void runTest(String mainClassName, String cp) {
        if (mainClassName.contains(".")) {
            mainClassName = mainClassName.replace(".", FILE_SEPARATOR);
        }
        ClassFileReader.setUserClasspath(cp);

        JClass clazz = ClassLoader.getInstance().loadClass(mainClassName);
        //初始化栈帧
        JThread thread=new JThread();
        //静态代码块是在初始化（clinit()）时执行的
        if (clazz.getInitState()== InitState.PREPARED) {
            //开始初始化
            clazz.initStart(clazz);
            Method clinit = clazz.getMethodInClass("<clinit>", "()V", true);
            if (clinit != null) {
                StackFrame clinitFrame = new StackFrame(thread, clinit, clinit.getMaxStack(), clinit.getMaxLocal());
                thread.pushFrame(clinitFrame);
                clazz.initSucceed(clazz);
                Interpreter.interpret(thread);
            }
        }
        Method main = clazz.getMethodInClass("main", "([Ljava/lang/String;)V", true);
        StackFrame mainFrame = new StackFrame(thread, main, main.getMaxStack(), main.getMaxLocal());
        thread.pushFrame(mainFrame);
        Interpreter.interpret(thread);
    }
}