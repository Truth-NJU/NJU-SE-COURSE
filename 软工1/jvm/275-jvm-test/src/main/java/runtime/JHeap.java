package runtime;

import runtime.struct.JObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class JHeap {
    private static JHeap jHeap = new JHeap();
    private static Set<JObject> objects;
    private static int maxSize = 50;
    private static int currentSize = 0;
    private static Map<Integer, Boolean> objectState;//true to present object is new added

    public static JHeap getInstance() {
        return jHeap;
    }

    private JHeap() {
        objects = new LinkedHashSet<>();
        objectState = new LinkedHashMap<>();
    }

}
