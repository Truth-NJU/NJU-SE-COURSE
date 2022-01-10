package com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.constant.ref;

import com.njuse.jvmfinal.classloader.classfileparser.constantpool.info.InterfaceMethodrefInfo;
import com.njuse.jvmfinal.memory.jclass.JClass;
import com.njuse.jvmfinal.memory.jclass.Method;
import com.njuse.jvmfinal.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;

@Getter
@Setter
public class InterfaceMethodRef extends MemberRef {
    private Method method;

    public InterfaceMethodRef(RuntimeConstantPool runtimeConstantPool, InterfaceMethodrefInfo interfaceMethodrefInfo) {
        super(runtimeConstantPool, interfaceMethodrefInfo);
        //method
    }

    public Method resolveInterfaceMethodRef(JClass clazz) throws ClassNotFoundException {
        return resolve(clazz);
    }

    public Method resolveInterfaceMethodRef() {
        try {
            resolveClassRef();
            return resolve(clazz);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Method resolve(JClass clazz) throws ClassNotFoundException {
        Method[] methods=clazz.getMethods();
        for (int i=0;i<methods.length;i++) {
            if (methods[i].getDescriptor().equals(descriptor)&&methods[i].getName().equals(name)) {
                return methods[i];
            }
        }

        JClass[] interfaces=clazz.getInterfaces();

        for (int i=0;i<interfaces.length;i++) {
            Method method1 =resolve(interfaces[i]);
            if (method1!= null) return  method1;
        }
        return null;
    }


    @Override
    public String toString() {
        return "InterfaceMethodRef to " + className;
    }

}
