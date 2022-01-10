package com.njuse.jvmfinal.memory;

import com.njuse.jvmfinal.memory.jclass.JClass;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;


public class MethodArea {
    private static MethodArea methodArea = new MethodArea();

    private MethodArea() {
        classMap = new LinkedHashMap<>();
    }

    @Setter
    @Getter
    private static Map<String, JClass> classMap;

    public static MethodArea getInstance() {
        return methodArea;
    }

    public JClass findClass(String className) {
        if (classMap.keySet().stream().anyMatch(name -> name.equals(className))) {
            return classMap.get(className);
        } else {
            return null;
        }
    }

    public void addClass(String className, JClass clazz) {
        classMap.put(className, clazz);
    }
}
